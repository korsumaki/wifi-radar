package com.korsumaki.wifiradar

import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


/**
 * [Workflow for requesting permissions](https://developer.android.com/training/permissions/requesting)
 *
 * If you conclude that your app needs to declare and request runtime permissions, complete these steps:
 *
 * 1. In your app's manifest file, declare the permissions that your app might need to request.
 * 2. Design your app's UX so that specific actions in your app are associated with specific runtime permissions. Users should know which actions might require them to grant permission for your app to access private user data.
 * 3. Wait for the user to invoke the task or action in your app that requires access to specific private user data. At that time, your app can request the runtime permission that's required for accessing that data.
 * 4. Check whether the user has already granted the runtime permission that your app requires. If so, your app can access the private user data. If not, continue to the next step.
 *    You must check whether you have that permission every time you perform an operation that requires that permission.
 * 5. Check whether your app should show a rationale to the user, explaining why your app needs the user to grant a particular runtime permission. If the system determines that your app shouldn't show a rationale, continue to the next step directly, without showing a UI element.
 *    If the system determines that your app should show a rationale, however, present the rationale to the user in a UI element. This rationale should clearly explain what data your app is trying to access, and what benefits the app can provide to the user if they grant the runtime permission. After the user acknowledges the rationale, continue to the next step.
 * 6. Request the runtime permission that your app requires in order to access the private user data. The system displays a runtime permission prompt, such as the one shown on the permissions overview page.
 * 7. Check the user's response, whether they chose to grant or deny the runtime permission.
 * 8. If the user granted the permission to your app, you can access the private user data. If the user denied the permission instead, gracefully degrade your app experience so that it provides functionality to the user, even without the information that's protected by that permission.
 *
 *
 * NOTE: Deviations/Missing things from PermissionController
 *  - Step 7. is done in MainActivity. Would be nice to have it in this class.
 *  - Step 7. is not explaining to user that feature is not available when permission is denied.
 *  - Step 8. is not continuing to task which was requiring permission, but user have to trigger it again.
 *  - triggerPermissionRequest() is relying that MainActivity has requestPermissionLauncher and requestMultiplePermissionsLauncher.
 */

/**
 * Permission Controller
 *
 * Idea for this controller
 *  - create PermissionController instance with list of permissions
 *  - use instance to check permissions
 *  - rationale is shown to user, if needed
 *
 *
 * @param activity          MainActivity
 * @param permissionList    List of required permissions
 * @param rationaleTitle    Title for rationale.
 * @param rationale         Rationale for permissions, to be shown to user if needed.
 *
 */
class PermissionController(val activity: Activity, val permissionList: List<String>, val rationaleTitle: String, val rationale: String) {

    /**
     * Check permission
     *
     * Do not trigger permission request.
     * This can be used when permission request dialog might be open, and it is not suitable
     * to request it again.
     *
     * @return true if permission is granted, false otherwise
     */
    fun checkPermission(): Boolean {

        // Step 4. Check whether user has already granted permission
        var userAlreadyGrantedAllRequiredPermissions = true
        for (permission in permissionList) {
            when (ContextCompat.checkSelfPermission(activity, permission)) {
                PackageManager.PERMISSION_GRANTED -> println("$permission -> PERMISSION_GRANTED")
                PackageManager.PERMISSION_DENIED -> {
                    println("$permission -> PERMISSION_DENIED")
                    userAlreadyGrantedAllRequiredPermissions = false
                }
            }
        }
        return userAlreadyGrantedAllRequiredPermissions
    }

    /**
     * Check permission, trigger permission request dialog when needed
     *
     * @return true if permission is granted, false otherwise
     */
    fun checkPermissionWithRequest(): Boolean {
        if (checkPermission()) {
            return true // Permission is already granted
        }

        // Step 5. Check whether app should show rationale to the user
        //         Rationale is required when user has first declined permission,
        //         but then tries to use the same feature again.
        var shouldShowRationaleToUser = false
        for (permission in permissionList) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                //println("$permission -> should show rationale")
                shouldShowRationaleToUser = true
            }
        }

        if (shouldShowRationaleToUser) {
            activity.runOnUiThread {
                AlertDialog.Builder(activity)
                    .setTitle(rationaleTitle)
                    .setMessage(rationale)
                    .setPositiveButton(R.string.ok) { _, _ ->
                        // Continue with requesting permissions
                        triggerPermissionRequest()
                    }
                    .setNegativeButton(R.string.cancel) { _, _ ->
                        // User cancelled the dialog
                    }
                    .create()
                    .show()
            }
            return false
        }
        // No need to show rationale

        // Step 6. Request runtime permission (system shows permission prompt)
        triggerPermissionRequest()

        return false
    }

    private fun triggerPermissionRequest() {
        // The registered ActivityResultCallback gets the result of this request.
        activity as MainActivity
        if (permissionList.size == 1) {
            activity.requestPermissionLauncher.launch(permissionList[0])
        }
        else {
            activity.requestMultiplePermissionsLauncher.launch(permissionList.toTypedArray())
        }
    }
}