package com.studita.presentation.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.studita.R
import com.studita.presentation.views.exercises.OutlineDrawView

class OutlineDrawActivity : DefaultActivity(), OutlineDrawView.DrawCompletedCallback {

    var shapes = listOf(
        R.drawable.outline_square,
        R.drawable.outline_rectangle,
        R.drawable.outline_oval,
        R.drawable.outline_triangle
    ).iterator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.outline_draw_view)
        findViewById<OutlineDrawView>(R.id.outlineDrawView).init(this, shapes.next())
        findViewById<View>(R.id.nextShape).setOnClickListener {
            if(!shapes.hasNext())
                shapes =listOf(
                    R.drawable.outline_square,
                    R.drawable.outline_rectangle,
                    R.drawable.outline_oval,
                    R.drawable.outline_triangle
                ).iterator()
            findViewById<OutlineDrawView>(R.id.outlineDrawView).init(this, shapes.next())
        }
    }

    override fun onDrawCompleted() {
        Toast.makeText(this, "outline completed!!!", Toast.LENGTH_LONG).show()
    }

}