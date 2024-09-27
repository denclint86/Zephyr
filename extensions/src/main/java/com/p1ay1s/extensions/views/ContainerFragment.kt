package com.p1ay1s.extensions.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.p1ay1s.dev.ui.FragmentControllerView
import com.p1ay1s.extensions.databinding.FragmentContainerBinding

open class ContainerFragment(private val map: LinkedHashMap<String, Fragment>) :
    Fragment(), FragmentControllerView.OnFragmentIndexChangedListener {

    private lateinit var binding: FragmentContainerBinding

    var controllerView: FragmentControllerView? = null
    private lateinit var currentIndex: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContainerBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            fragmentControllerViewContainer.run {
                init(childFragmentManager, map)
                controllerView = this
                setOnFragmentIndexChangeListener(this@ContainerFragment)
            }
        }
    }

    override fun onFragmentIndexChanged(index: String) {
        currentIndex = index
    }

    fun getIndex(): String? {
        if (::currentIndex.isInitialized)
            return currentIndex
        return null
    }
}
