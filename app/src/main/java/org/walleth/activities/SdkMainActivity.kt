@file:JvmName("SdkMainActivity")
package org.walleth.activities

import android.app.Activity
import android.content.res.Configuration
import kotlinx.android.synthetic.main.activity_main.*
import org.ligi.kaxt.startActivityFromClass

class SdkMainActivity : MainActivity() {

    fun setGameInfo(newGameActivity: Activity, gameIconId : Int, gameDescrStr : String) {
        game_icon.setBackgroundResource(gameIconId)
        game_descr.setText(gameDescrStr)
        if (newGameActivity is Activity)
        game_view.setOnClickListener {
            startActivityFromClass(newGameActivity::class)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

}
