package sk.stuba.fei.uim.dp.attendance.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
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
    }


}