package sample.app.tobeylin.androidpractice.viewpager2

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_view_pager2.*
import sample.app.tobeylin.androidpractice.R

class ViewPager2Fragment : Fragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i("ViewPager2", "$this: onAttach")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("ViewPager2", "$this: onCreateView")
        return inflater.inflate(R.layout.fragment_view_pager2, container, false).apply {
            this.setBackgroundColor(getBackgroundColor())
        }
    }

    override fun onStart() {
        super.onStart()
        Log.i("ViewPager2", "$this: onStart")
    }

    override fun onResume() {
        super.onResume()
        viewPager2ContentTitle.text = getTitle()
        Log.i("ViewPager2", "$this: onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.i("ViewPager2", "$this: onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.i("ViewPager2", "$this: onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("ViewPager2", "$this: onDestroyView")
    }

    private fun getBackgroundColor(): Int = requireArguments().getInt(ARG_BG_COLOR)

    private fun getTitle(): String = requireArguments().getString(ARG_TITLE, "---")

    companion object {

        const val ARG_BG_COLOR = "ARG_BG_COLOR"
        const val ARG_TITLE = "ARG_TITLE"

        @JvmStatic
        fun newInstance(bgColor: Int, title: String): ViewPager2Fragment = ViewPager2Fragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_BG_COLOR, bgColor)
                putString(ARG_TITLE, title)
            }
        }

    }
}