package com.krish.foody.ui.fragments.recipes

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.krish.foody.R
import com.krish.foody.adapter.ChipAdapter
import com.krish.foody.adapter.RBtnClick
import com.krish.foody.adapter.RecipesAdapter
import com.krish.foody.databinding.FragmentRecipesBinding
import com.krish.foody.models.FoodRecipe
import com.krish.foody.models.Result
import com.krish.foody.util.*
import com.krish.foody.util.NetworkResult.*
import com.krish.foody.viewmodel.MainViewModel
import com.krish.foody.viewmodel.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "RecipesFragment"

@AndroidEntryPoint
class RecipesFragment : Fragment(), RBtnClick {

    private val args by navArgs<RecipesFragmentArgs>()
    private lateinit var mViewModel: MainViewModel
    private lateinit var mRecipesViewModel: RecipesViewModel
    private val mRecipeAdapter by lazy {
        RecipesAdapter(requireContext(), this@RecipesFragment)
    }

    private lateinit var binding: FragmentRecipesBinding
    private lateinit var networkListener: NetworkListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        mRecipesViewModel = ViewModelProvider(requireActivity())[RecipesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRecipesBinding.inflate(inflater, container, false)
        setUpRecyclerView()
        mRecipesViewModel.readBackOnline.observe(viewLifecycleOwner, Observer {
            mRecipesViewModel.backOnline = it
        })

        networkListener = NetworkListener()
        lifecycleScope.launchWhenStarted {
            networkListener.checkNetworkAvailability(requireContext())
                .collect { status ->
                    Log.d("NetworkListener", status.toString())
                    mRecipesViewModel.networkStatus = status
                    mRecipesViewModel.showNetworkStatus()
                    readDatabase()
                }

        }

        mViewModel.recipeResponse.observe(viewLifecycleOwner, Observer { networkResponse ->
            mViewModel.readRecipe.observe(viewLifecycleOwner, Observer { database ->
                if (networkResponse is Error && database.isNullOrEmpty()) {
                    binding.errorImage.visibility = View.VISIBLE
                    binding.errorMessage.visibility = View.VISIBLE
                    binding.errorMessage.text = networkResponse.message.toString()
                } else if (networkResponse is Loading) {
                    binding.errorImage.visibility = View.INVISIBLE
                    binding.errorMessage.visibility = View.INVISIBLE
                } else if (networkResponse is Success) {
                    binding.errorImage.visibility = View.INVISIBLE
                    binding.errorMessage.visibility = View.INVISIBLE
                }
            })
        })
        binding.floatingActionButtonSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchApiData(query)

                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
        binding.floatingActionButtonSearch.setOnClickListener {
            binding.floatingActionButtonSearchV.visibility = View.VISIBLE
            binding.floatingActionButtonSearchView.onActionViewExpanded()
        }

        binding.floatingActionButton.setOnClickListener {
            if (mRecipesViewModel.networkStatus) {
                findNavController().navigate(R.id.action_recipesFragment_to_recipesBottomSheet)
            } else {
                mRecipesViewModel.showNetworkStatus()
            }
        }
        return binding.root
    }



    private fun setUpRecyclerView() {
        binding.recyclerview.adapter = mRecipeAdapter
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }







    private fun readDatabase() {
        lifecycleScope.launch {
            mViewModel.readRecipe.observeOnce(viewLifecycleOwner, Observer { database ->
                if (database.isNotEmpty() && !args.backFromBottomSheet) {
                    Log.d(TAG, "readDatabase called!")
                    mRecipeAdapter.setData(database[0].foodRecipe)
                    hideShimmerEffect()
                } else {
                    requestApiData()
                }
            })
        }
    }

    private fun requestApiData() {
        Log.d(TAG, "requestApiData called!")
        mViewModel.getRecipes(mRecipesViewModel.applyQueries())
        mViewModel.recipeResponse.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Success -> {
                    hideShimmerEffect()
                    response.data?.let {
                        mRecipeAdapter.setData(it)
                        mRecipesViewModel.saveMealAndDietType()
                    }
                }
                is Error -> {
                    if (response.message == "Loading..."){
                        mViewModel.getRecipes(mRecipesViewModel.applyQueries())
                    }else {
                        hideShimmerEffect()
                        loadDataFromCache()
                        Toast.makeText(
                            requireContext(),
                            response.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                is Loading -> {
                    showShimmerEffect()
                }
            }
            setResponse(response)
        })
    }

    private fun searchApiData(searchQuery: String) {
        showShimmerEffect()
        mViewModel.searchRecipes(mRecipesViewModel.applySearchQuery(searchQuery))
        mViewModel.searchedRecipesResponse.observe(viewLifecycleOwner, Observer { response ->
            binding.floatingActionButtonSearchV.visibility = View.GONE
            when (response) {
                is Success -> {
                    hideShimmerEffect()
                    val foodRecipe = response.data
                    foodRecipe?.let {
                        mRecipeAdapter.setData(it)
                    }
                }
                is Error -> {
                    hideShimmerEffect()
                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Loading -> {
                    showShimmerEffect()
                }
            }
        })
    }

    private fun setResponse(response: NetworkResult<FoodRecipe>?) {
        if (response is NetworkResult.Error && mViewModel.readRecipe.value.isNullOrEmpty()) {
            binding.errorImage.visibility = View.VISIBLE
            binding.errorMessage.visibility = View.VISIBLE
            binding.errorMessage.text = response.message
        } else {
            binding.errorImage.visibility = View.INVISIBLE
            binding.errorMessage.visibility = View.INVISIBLE
        }
    }

    private fun loadDataFromCache() {
        lifecycleScope.launch {
            mViewModel.readRecipe.observe(viewLifecycleOwner, Observer { database ->
                if (database.isNotEmpty()) {
                    mRecipeAdapter.setData(database[0].foodRecipe)
                }
            })
        }
    }


    private fun showShimmerEffect() {
        binding.shimmerFrameLayout.startShimmer()
        binding.recyclerview.visibility = View.GONE
        binding.shimmerFrameLayout.visibility = View.VISIBLE

    }

    private fun hideShimmerEffect() {
        binding.shimmerFrameLayout.visibility = View.GONE
        binding.recyclerview.visibility = View.VISIBLE

    }

    override fun getRecipeOnClick(bundle: Result) {
        AdsLoader.showAds(requireActivity()) {
            val action = RecipesFragmentDirections.actionRecipesFragmentToDetailsActivity(bundle)
            findNavController().navigate(action)
        }
    }




}