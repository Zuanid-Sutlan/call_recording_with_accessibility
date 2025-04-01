package com.example.call_recording_with_accessibility

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.example.call_recording_with_accessibility.components.DropDown
import com.example.call_recording_with_accessibility.ui.theme.Call_recording_with_accessibilityTheme

class MainActivity : ComponentActivity() {

    private lateinit var recorder: Recorder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        recorder = Recorder(this)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissionsToRequest = getAppDeclaredPermissions(this)
            if (permissionsToRequest != null)
                requestPermissions(permissionsToRequest, 0)
        }

        setContent {
            val context = LocalContext.current
            var textValue by remember { mutableStateOf("03456575620") }

            var audioSource by remember { mutableStateOf("") }

            Call_recording_with_accessibilityTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Button(
                            onClick = {
//                                updateRecordingSourceButton()
                                val source = Recorder.getSavedAudioSource(this@MainActivity)
                                audioSource = "recording source:${source.name}"
                            }
                        ) { Text(text = audioSource ?: "") }

                        Spacer(modifier = Modifier.height(24.dp))
                        val audioSources = AudioSource.getAllSupportedValues(this@MainActivity)
                        val items = arrayOfNulls<CharSequence>(audioSources.size)
                        for (i in 0 until audioSources.size)
                            items[i] = audioSources[i].name
                        DropDown(
                            itemList = items
                        ) {
                            Recorder.setSavedAudioSource(this@MainActivity, audioSources[it])
//                            updateRecordingSourceButton()
                            val source = Recorder.getSavedAudioSource(this@MainActivity)
                            audioSource = "recording source:${source.name}"
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            value = textValue,
                            onValueChange = {
                                textValue = it
                            },
                            label = {
                                Text("Enter phone number")
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (textValue.isNotBlank()) {
//                                    if (!isAccessibilityServiceEnabled()) {
//                                    } else{
                                        dialPhone(context, textValue)
//                                    }
                                }
                            }
                        ) { Text(text = "Call") }

                        val source = Recorder.getSavedAudioSource(this@MainActivity)
                        audioSource = "recording source:${source.name}"

                        Button(
                            onClick = {
                                recorder.playRecording()
                            }
                        ) { Text(text = "play recording") }

                        Button(
                            onClick = {
                                requestAccessibilityPermission()
                            }
                        ) {
                            Text(text = "request accessibility permission")
                        }

                    }
                }
            }

            fun updateRecordingSourceButton() {
                val source: AudioSource = Recorder.getSavedAudioSource(this)
                audioSource = "recording source:${source.name}"
            }
        }
    }

    companion object{
        @SuppressLint("MissingPermission")
        @JvmStatic
        fun dialPhone(context: Context, phone: String) {
            context.startActivity(Intent(Intent.ACTION_CALL, "tel:$phone".toUri()))
        }

        @JvmStatic
        fun getAppDeclaredPermissions(context: Context): Array<out String>? {
            val pm = context.packageManager
            try {
                val packageInfo = pm.getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
                return packageInfo.requestedPermissions ?: return null
            } catch (ignored: PackageManager.NameNotFoundException) {
                //we should always find current app
            }
            throw RuntimeException("cannot find current app?!")
        }
    }


    private fun requestAccessibilityPermission() {
        var intent = Intent("com.samsung.accessibility.installed_service")
        if (intent.resolveActivity(packageManager) == null) {
            intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        }
        val extraFragmentArgKey = ":settings:fragment_args_key"
        val extraShowFragmentArguments = ":settings:show_fragment_args"
        val bundle = Bundle()
        val showArgs: String = "${packageName}/${MyAccessibilityService::class.java.name}"
        bundle.putString(extraFragmentArgKey, showArgs)
        intent.putExtra(extraFragmentArgKey, showArgs)
        intent.putExtra(extraShowFragmentArguments, bundle)
        try {
            startActivity(intent)
        } catch (e: Exception) {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY))
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val expectedComponentName = ComponentName(packageName, MyAccessibilityService::class.java.name)

        val enabledServicesSetting = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false

        // The setting is a ':' separated list of component names
        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        colonSplitter.setString(enabledServicesSetting)

        while (colonSplitter.hasNext()) {
            val componentNameString = colonSplitter.next()
            val enabledService = ComponentName.unflattenFromString(componentNameString)

            if (enabledService != null && enabledService == expectedComponentName) {
                return true
            }
        }

        return false
    }

}

