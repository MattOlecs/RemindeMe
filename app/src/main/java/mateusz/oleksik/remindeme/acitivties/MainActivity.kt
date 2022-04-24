package mateusz.oleksik.remindeme.acitivties

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.preference.PreferenceManager
import mateusz.oleksik.remindeme.R
import mateusz.oleksik.remindeme.databinding.ActivityMainBinding
import mateusz.oleksik.remindeme.fragments.FoodCreateFragment
import mateusz.oleksik.remindeme.fragments.FoodListFragment
import mateusz.oleksik.remindeme.fragments.SettingsFragment
import mateusz.oleksik.remindeme.interfaces.IFoodCreateDialogListener
import mateusz.oleksik.remindeme.models.Food
import mateusz.oleksik.remindeme.utils.Constants

class MainActivity : AppCompatActivity(), IFoodCreateDialogListener {

    private lateinit var foodListFragment: FoodListFragment
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.myToolbar)

        if (savedInstanceState == null) {
            generateFoodList()

            binding.addFoodActionButton.setOnClickListener {
                openCreateFoodDialog()
            }
        }
    }

    private fun generateFoodList() {
        val fragmentTag = "foodList"
        supportFragmentManager
            .beginTransaction()
            .add(R.id.root_layout, FoodListFragment.newInstance(), fragmentTag)
            .addToBackStack("main")
            .commit()
        supportFragmentManager.executePendingTransactions()

        foodListFragment =
            supportFragmentManager.findFragmentByTag(fragmentTag) as FoodListFragment
    }

    private fun openCreateFoodDialog() {
        val dialog = FoodCreateFragment(this)
        dialog.show(supportFragmentManager, "createFoodDialog")
        val hour = PreferenceManager.getDefaultSharedPreferences(this).getString("notification_hour", "8").toString()
        val minute = PreferenceManager.getDefaultSharedPreferences(this).getString("notification_minute", "8").toString()
        Toast.makeText(this, "${hour}:${minute}", Toast.LENGTH_SHORT).show()
    }

    override fun onCreatedFood(food: Food) {
        Log.i(
            Constants.DebugLogTag,
            "Main activity communicated new food creation to Adapter. Food: ${food.name}"
        )
        foodListFragment.addFoodToAdapter(food)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_action_buttons, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.settings_button -> {
            supportFragmentManager
                .beginTransaction()
                .replace(foodListFragment.id, SettingsFragment())
                .addToBackStack("main")
                .commit()
            supportFragmentManager.executePendingTransactions()
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}