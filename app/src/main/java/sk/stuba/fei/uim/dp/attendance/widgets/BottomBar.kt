package sk.stuba.fei.uim.dp.attendance.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import sk.stuba.fei.uim.dp.attendance.R

class BottomBar : ConstraintLayout {

    constructor(context: Context) : super(context){
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        init()
    }

    fun init(){
        val layout = LayoutInflater.from(context)
            .inflate(R.layout.widget_bottom_bar, this, false)
        addView(layout)

        layout.findViewById<ImageButton>(R.id.create_activity_button).setOnClickListener {
            it.findNavController().navigate(R.id.action_to_add_activity)
        }

        layout.findViewById<ImageView>(R.id.image_home).setOnClickListener {
            it.findNavController().navigate(R.id.action_to_home)
        }

        layout.findViewById<ImageView>(R.id.image_profile).setOnClickListener {
            it.findNavController().navigate(R.id.action_to_profile)
        }
    }


}