package com.example.training

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.location.Location
import android.os.*
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.training.RoomDataBase.NewsViewModel
import com.example.training.pagination.NewsListViewModel
import com.example.training.pagination.Pagination
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener, OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {

    private var startTime = 0L

    var api = ApiService.create()
    private lateinit var disposable: Disposable
    var timeInMilliseconds = 0L
    var timeSwapBuff = 0L
    var updatedTime = 0L
    lateinit var timerFragment : TimerFragment
    lateinit var sharePerfereneces : SharedPreferences
    lateinit var googleMap : GoogleMap
    private var mNotificationManager: NotificationManager? = null
    private var mBuilder: NotificationCompat.Builder? = null
    val NOTIFICATION_CHANNEL_ID = "10001"
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var userLocation: LatLng
    lateinit var newsViewModel : NewsViewModel

    override fun onMapReady(p0: GoogleMap) {
        Log.i("MainActivity", "onMapReady: ")
        googleMap = p0
        Log.i("MainActivity", "onMapReady: ")
        p0.uiSettings.isCompassEnabled = false
        p0.uiSettings.isMyLocationButtonEnabled = false
        p0.setOnMarkerClickListener(this)
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setDeniedMessage("Please enable your location permissions to continue using Training.You can turn on permissions in  [App Settings] > [Permission]\")")
                .setPermissions(
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                )
                .check();


    }

    override fun onMarkerClick(p0: Marker?): Boolean {

        if(p0 !=null){
            startActivity(Intent(this,Pagination::class.java))

        }
        return false
    }

    private var permissionListener = object : PermissionListener {

        override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
            Log.i("MainActivity", "Permission was denied")
        }

        @SuppressLint("MissingPermission")
        override fun onPermissionGranted() {
            fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        Log.d("MainActivity", "onPermissionGranted: addOnSuccessListener")
                        if (location?.latitude != null) {
                            Log.d("MainActivity", "onPermissionGranted: addOnSuccessListener location is not null")
                            userLocation = LatLng(location.latitude, location.longitude)
                            Log.i("MainActivity", "onPermissionGranted: userLocation $userLocation")
                            googleMap.isMyLocationEnabled = true
                            moveToCamera()
                            googleMap.addMarker(MarkerOptions().position(userLocation).title("User Location"))

                        } else {
                            Log.i(
                                    "MainActivity",
                                    "onPermissionGranted (line 125): Unable to fetch user location"
                            )
                        }
                    }

        }

    }

    fun moveToCamera(){
        try {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f));
        }catch (ex:Exception){
            Log.i("MainActivity", "moveToCamera: Exception ${ex} ")
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


            sharePerfereneces = getSharedPreferences(packageName, Context.MODE_PRIVATE)

        timerFragment = supportFragmentManager.findFragmentById(R.id.timer_fragment) as TimerFragment

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        newsViewModel  = ViewModelProviders.of(this).get(NewsViewModel::class.java)

        getNews()
        setButtonListener()
    }

    private fun setButtonListener(){
        start_button.setOnClickListener(this)
        stop_button.setOnClickListener(this)
        show_notification_button.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v){
            start_button -> {
//                startTime = SystemClock.uptimeMillis();
//                customHandler.postDelayed(updateTimerThread, 0);
                    val intent = Intent(this@MainActivity, TimerService::class.java)
                    startService(intent)
//                    registerReceiver(broadcastReceiver, IntentFilter(TimerService.BROADCAST_ACTION))
//                    val intent = Intent(this@MainActivity, CountDownTimerService::class.java)
//                    startService(intent)
                    registerReceiver(broadcastReceiver, IntentFilter(TimerService.BROADCAST_ACTION))


            }
            stop_button -> {
                val intent = Intent(this@MainActivity, TimerService::class.java)
                unregisterReceiver(broadcastReceiver);
                stopService(intent);
//                val intent = Intent(this@MainActivity, CountDownTimerService::class.java)
//                unregisterReceiver(broadcastReceiver);
//                stopService(intent);
            }
            show_notification_button -> {
                addNotification()
                startActivity(Intent(this,CountDownTimer::class.java))
            }
        }
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                updateUI(intent)
            }
        }
    }

    private fun updateUI(intent: Intent) {
        val timer = intent.getLongExtra("timer", 0)
        Log.i("MainActivity", "updateUI: timer $timer")
      val time = timer.toInt() / 1000
        Log.d("Hello", "Time $time")
        val secs = time % 60
        val mins = time / 60

        timerFragment.displayTime(secs, mins)
        sharePerfereneces.edit().putLong("time", timer).apply()

    }

    override fun onPause() {
        super.onPause()
//        unregisterReceiver(broadcastReceiver)
    }
    override fun onResume() {
        super.onResume()


            registerReceiver(broadcastReceiver, IntentFilter(TimerService.BROADCAST_ACTION))
//            registerReceiver(broadcastReceiver, IntentFilter(CountDownTimerService.COUNTDOWN_BR))

    }

    override fun onStart() {
        super.onStart()
        registerReceiver(broadcastReceiver, IntentFilter(TimerService.BROADCAST_ACTION))
//        registerReceiver(broadcastReceiver, IntentFilter(CountDownTimerService.COUNTDOWN_BR))

    }

    private fun addNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val intent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0)
        val builder = NotificationCompat.Builder(this)
                .addAction(android.R.drawable.ic_dialog_alert, "Open",
                        intent)
                .setOngoing(true)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("Unique Andro Code")
                .setContentText("welcome To Unique Andro Code")
                .setContentIntent(intent)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance)
            builder.setChannelId(NOTIFICATION_CHANNEL_ID)
            manager?.createNotificationChannel(notificationChannel)
        }
        manager.notify(0 /* Request Code */, builder?.build())

    }

    fun getNews(){
        disposable =  api.getNews(1, 10)

            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                Log.i("MainActivity", "apiFuncation:  response $it")
                newsViewModel.addNews(it.news[0])
                getNewsViewModel()
            }, {
                Log.i("MainActivity", "apiFuncation:  response erro ${it.localizedMessage}")

            })

            }

        fun getNewsViewModel() {
            newsViewModel.getNews().observe(this, Observer {
                Log.i("ManiActivity", "getNews: newsViewModel $it")
            })
        }

    override fun onDestroy() {
        super.onDestroy()
        if (::disposable.isInitialized) {
            disposable.dispose()
        }
    }



}
