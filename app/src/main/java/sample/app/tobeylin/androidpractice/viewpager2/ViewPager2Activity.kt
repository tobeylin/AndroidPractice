package sample.app.tobeylin.androidpractice.viewpager2

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_view_pager_2.*
import sample.app.tobeylin.androidpractice.R

class ViewPager2Activity : AppCompatActivity() {

    private val addFragmentRunnable: Runnable = Runnable {
        with(viewpager) {
            visibility = View.VISIBLE

            adapter = ViewPager2Adapter(this@ViewPager2Activity).apply {
                addFragment(ViewPager2Fragment.newInstance(Color.RED, "This title is set during onResume"))
                addFragment(ViewPager2Fragment.newInstance(Color.YELLOW, "This title is set during onResume"))
                addFragment(ViewPager2Fragment.newInstance(Color.BLUE, "This title is set during onResume"))
                addFragment(ViewPager2Fragment.newInstance(Color.GREEN, "This title is set during onResume"))
                addFragment(ViewPager2Fragment.newInstance(Color.DKGRAY, "This title is set during onResume"))
                addFragment(ViewPager2Fragment.newInstance(Color.CYAN, "This title is set during onResume"))
            }

            // FIXME: https://issuetracker.google.com/issues/143989556
//            viewpager.offscreenPageLimit = 1

            // FIXME: https://issuetracker.google.com/issues/145569952
//            viewpager.currentItem = 2
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_view_pager_2)

        viewpager.postDelayed(addFragmentRunnable, 3000)
    }

}