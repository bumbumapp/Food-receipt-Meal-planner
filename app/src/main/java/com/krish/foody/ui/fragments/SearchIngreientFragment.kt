package com.krish.foody.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat.recreate
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.krish.foody.adapter.*
import com.krish.foody.databinding.FragmentSearchIngredientsBinding
import com.krish.foody.models.Result
import com.krish.foody.ui.fragments.recipes.IngredientsAdapter
import com.krish.foody.util.*

import com.krish.foody.viewmodel.SearchIngredientsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SearchIngreientFragment:Fragment(),ChipItemClickListener,ChipClickteditemListner,RBtnClick,SearchAdapterItemClickedListener {
    private var _binding: FragmentSearchIngredientsBinding? = null
    private val binding get() = _binding!!
    private var chipAdapter: ChipAdapter? = null
    private var selectedChipAdapter: SelectedChipAdapter? = null
    private var ingredientsList:List<String>? = null
    private var maxToShowed = 74
    private val viewModel:SearchIngredientsViewModel by viewModels()
    private val mRecipeAdapter by lazy {
        IngredientsAdapter(this)
    }
    private val msearchAdapter by lazy {
        ItemAdapter(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchIngredientsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        selectedChipAdapter= SelectedChipAdapter(requireContext(),binding.selectedChipGroup,this)
        getIngredientsList()
        observeSelectedIngredients()
        clickEditText()
        getSearchedIngredinetsRecipes()
        setUpRecyclerView()
        binding.recipeFabBack.setOnClickListener {
            binding.linear.visibility = View.VISIBLE
            binding.constraint.visibility = View.GONE
        }
        super.onViewCreated(view, savedInstanceState)
    }
    private fun setUpRecyclerView() {
        binding.searchSuggestions.adapter = msearchAdapter
        binding.searchSuggestions.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
        binding.recyclerview.adapter = mRecipeAdapter
        binding.recyclerview.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        showShimmerEffect()
    }

    private fun getSearchedIngredinetsRecipes() {
        binding.recipeFabSearch.setOnClickListener {
            binding.linear.visibility = View.GONE
            binding.constraint.visibility = View.VISIBLE
            showShimmerEffect()
            viewModel.searchRecipes()
        }
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.recipeResponse.collectLatest {
                    when(it){
                        is NetworkResult.Error -> {
                            hideShimmerEffect()
                            Snackbar.make(requireView(),it.message.toString(), BaseTransientBottomBar.LENGTH_LONG).show()
                            binding.linear.visibility = View.VISIBLE
                            binding.constraint.visibility = View.GONE
                        }
                        is NetworkResult.Loading -> {
                            showShimmerEffect()
                        }
                        is NetworkResult.Success -> {
                            hideShimmerEffect()
                            mRecipeAdapter.setData(it.data?.results ?: emptyList() )
                        }
                    }
                }

            }
        }
    }
    private fun showShimmerEffect() {
        binding.shimmerFrameLayout.visibility = View.VISIBLE
        binding.shimmerFrameLayout.startShimmer()
        binding.recyclerview.visibility = View.GONE

    }

    private fun hideShimmerEffect() {
        binding.shimmerFrameLayout.visibility = View.GONE
        binding.recyclerview.visibility = View.VISIBLE

    }

    private fun clickEditText() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                binding.editText.addTextChangedListener {txt->
                    if (txt?.isNotEmpty() == true){
                        binding.searchSuggestions.visibility = View.VISIBLE
                        ingredientsList?.let { it1 -> msearchAdapter.updateData(it1.filter { it.lowercase().contains(txt.toString().lowercase())  }) }
                    }else {
                        binding.searchSuggestions.visibility = View.GONE
                    }
                }
            }
        }

        binding.addButton.setOnClickListener {
            binding.searchSuggestions.visibility = View.GONE
            binding.recipeFabSearch.visibility = View.VISIBLE
            val editextText = binding.editText.text.toString()
            if (editextText.isNotEmpty() || editextText.isNotBlank()){
                viewModel.setSelectedIngredient(editextText.lowercase(),true)
                itemClickedForRefresh()
            }

        }
    }

    private fun itemClickedForRefresh(){
        chipAdapter?.addChips(
            ingredientsList?.subList(0,maxToShowed) ?: emptyList(),
            maxToShowed,
            viewModel.listOfSelectedIngredinets
        )
    }

    private fun observeSelectedIngredients() {
        viewModel.selectedIngredinets.observe(viewLifecycleOwner){
            if (it.isNotEmpty()) {
                binding.myIngredient.visibility = View.VISIBLE
                selectedChipAdapter?.addChips(it.toList())
            }
            else{
                binding.myIngredient.visibility = View.GONE
            }
            Log.d("selectedIngredinets3",""+it)

        }
    }

    private fun getIngredientsList() {
        lifecycleScope.launch(Dispatchers.IO){
            ingredientsList = getIngredients(requireContext())
            withContext(Dispatchers.Main){
                chipAdapter = ChipAdapter(requireContext(),binding.chipGroup,this@SearchIngreientFragment)
                chipAdapter?.addChips(
                    ingredientsList?: emptyList(),
                    74,
                    viewModel.listOfSelectedIngredinets
                )
            }

        }

    }
    override fun itemClicked(maxItemsToShow: Int) {
        if (maxItemsToShow < 574){
            chipAdapter?.addChips(
                ingredientsList?.subList(0,maxItemsToShow+50) ?: emptyList(),
                maxItemsToShow+50,
                viewModel.listOfSelectedIngredinets
            )
            maxToShowed = maxItemsToShow + 50
        }
    }

    override fun clickItemForSelect(element: String) {
        viewModel.setSelectedIngredient(element.lowercase(),false)
    }

    override fun chipClicked(element: String) {
        viewModel.setSelectedIngredient(element.lowercase(),false)
        itemClickedForRefresh()
    }

    override fun getRecipeOnClick(bundle: Result) {
        AdsLoader.showAds(requireActivity()){
            val action = SearchIngreientFragmentDirections.actionSearchIngreientFragmentToDetailsActivity(bundle)
            findNavController().navigate(action)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    override fun addItem(item: String) {
        binding.searchSuggestions.visibility = View.GONE
        viewModel.setSelectedIngredient(item.lowercase(),true)
        itemClickedForRefresh()
    }
}