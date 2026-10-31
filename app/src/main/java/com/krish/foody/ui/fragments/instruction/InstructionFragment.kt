package com.krish.foody.ui.fragments.instruction

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.krish.foody.databinding.FragmentInstructionBinding
import com.krish.foody.databinding.InstructionStepRowBinding
import com.krish.foody.models.Result
import com.krish.foody.util.Constants


class InstructionFragment : Fragment() {

    private lateinit var binding: FragmentInstructionBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentInstructionBinding.inflate(inflater, container, false)

        val myBundle: Result? = arguments?.getParcelable(Constants.RECIPE_RESULT_KEY)

        // Native steps from the API. The source page used to be the only view and on many sites
        // it rendered blank or showed just an ad banner.
        val lines = InstructionSteps.from(myBundle?.analyzedInstructions)
        if (lines.isNotEmpty()) {
            showSteps(inflater, lines)
        } else if (!myBundle?.sourceUrl.isNullOrBlank()) {
            showSourcePage(myBundle!!.sourceUrl!!)
        } else {
            binding.instructionStepsScroll.visibility = View.GONE
            binding.instructionEmpty.visibility = View.VISIBLE
        }

        return binding.root
    }

    private fun showSteps(inflater: LayoutInflater, lines: List<InstructionLine>) {
        lines.forEach { line ->
            val row = InstructionStepRowBinding.inflate(inflater, binding.instructionSteps, false)
            if (line.section != null) {
                row.stepSection.text = line.section
                row.stepSection.visibility = View.VISIBLE
            }
            row.stepNumber.text = line.number.toString()
            row.stepText.text = line.text
            binding.instructionSteps.addView(row.root)
        }
    }

    private fun showSourcePage(url: String) {
        binding.instructionStepsScroll.visibility = View.GONE
        binding.instructionWebView.visibility = View.VISIBLE
        binding.instructionProgress.visibility = View.VISIBLE
        binding.instructionWebView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                binding.instructionProgress.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                binding.instructionProgress.visibility = View.GONE
            }
        }
        binding.instructionWebView.loadUrl(url)
    }
}
