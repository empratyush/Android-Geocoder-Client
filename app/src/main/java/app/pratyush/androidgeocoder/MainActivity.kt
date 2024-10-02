package app.pratyush.androidgeocoder

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.MutableLiveData
import app.pratyush.androidgeocoder.ui.theme.AndroidGeocoderTheme

private const val TAG = "MainActivity1"

class MainActivity : ComponentActivity() {

    private val result: MutableLiveData<List<Address>> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            AndroidGeocoderTheme {

                val items = result.observeAsState(emptyList())

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    Column(modifier = Modifier.padding(innerPadding)) {
                        if (!Geocoder.isPresent()) {
                            Text("device is lacking geocoder service/provider")
                            return@Scaffold
                        }

                        LazyColumn {

                            if (items.value.isEmpty()) {
                                item {
                                    Text("no data yet")
                                }
                            }

                            items(items.value) { item ->
                                Text("${item.latitude} ${item.longitude} ${item.countryName} ${item.postalCode}")
                            }

                        }
                        LocationInputField(::loadInfo)
                    }

                }
            }
        }
    }

    private fun loadInfo(latitude: Double, longitude: Double, maxResult: Int = 10) {

        Geocoder(this)
            .getFromLocation(
                latitude,
                longitude,
                maxResult
            ) { addresses ->
                Log.d(TAG, "got the result : ${addresses.size}")
                result.postValue(addresses)
            }
    }
}

@Composable
fun LocationInputField(
    onValuesSubmitted: (Double, Double) -> Unit
) {

    //https://en.wikipedia.org/wiki/Googleplex
    //Coordinates: 37.422°N 122.084°W
    var longitude by remember { mutableDoubleStateOf(-122.084) }
    var latitude by remember { mutableDoubleStateOf(37.422) }

    Column(modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = "$latitude",
            onValueChange = { newValue ->
                latitude = newValue.toDoubleOrNull() ?: 0.0
            },
            label = { Text("Integer 1") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        TextField(
            value = "$longitude",
            onValueChange = { newValue ->
                longitude = newValue.toDoubleOrNull() ?: 0.0
            },
            label = { Text("Integer 2") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Button(onClick = { onValuesSubmitted(latitude, longitude) }) {
            Text("Submit")
        }
    }
}
