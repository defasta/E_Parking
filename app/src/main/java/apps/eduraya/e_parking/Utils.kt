package apps.eduraya.e_parking

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import apps.eduraya.e_parking.data.network.Resource
import com.google.android.material.snackbar.Snackbar
import java.text.NumberFormat
import java.util.*

fun <A : Activity> Activity.startNewActivity(activity: Class<A>) {
    Intent(this, activity).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }
}

fun <A : Activity> Activity.startAnActivity(activity: Class<A>) {
    Intent(this, activity).also {
        startActivity(it)
    }
}

fun View.visible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun View.enable(enabled: Boolean) {
    isEnabled = enabled
    alpha = if (enabled) 1f else 0.5f
}

fun View.snackbar(message: String, action: (() -> Unit)? = null) {
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    action?.let {
        snackbar.setAction("Retry") {
            it()
        }
    }
    snackbar.show()
}

//fun Fragment.logout() = lifecycleScope.launch {
//    if (activity is HomeActivity) {
//        (activity as HomeActivity).performLogout()
//    }
//}

fun Fragment.handleApiError(
    failure: Resource.Failure,
    retry: (() -> Unit)? = null
) {
//    when {
////        failure.isNetworkError -> requireView().snackbar(
////            failure.errorBody?.string().toString(),
////            retry
////        )
//////        failure.errorCode == 422 -> {
//////            if (this is LoginFragment) {
//////                requireView().snackbar("You've entered incorrect email or password")
//////            } else {
//////                logout()
//////            }
//////        }
//////        failure.errorCode == 422 -> {
//////            if (this is SignUpFragment) {
//////                requireView().snackbar("Password dan konfirmasi password harus sama!")
//////            } else {
//////                logout()
//////            }
//////        }
////        else -> {
//            val error = failure.errorBody?.string().toString()
//            requireView().snackbar(error)
//        //}
    //}
    val error = failure.errorCode.toString()
    requireView().snackbar(error)
}

fun Activity.handleApiErrorActivity(
    failure: Resource.Failure,
    retry: (() -> Unit)? = null
) {
    val error = failure.errorBody.toString()
    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
}

fun rupiah(number: Double): String {
    val LocaleID = Locale("in", "ID")
    val numberFormat = NumberFormat.getCurrencyInstance(LocaleID)
    return numberFormat.format(number).toString()
}


