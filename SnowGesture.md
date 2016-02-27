# SnowGesture #

This class makes it easier for developers to implement simple gestures. The class recognizes 4 different directions: Up,
Down, Left and Right.

# How to implement #

First, ofcourse, initialise an instance of `SnowGesture`. Override the `boolean onTouchEvent(MotionEvent event)` of your Activity and have it call `SnowGesture.touchEvent(event)`, where event is the MotionEvent from the `onTouchEvent()`.
And to get the recognition to work you have two options:
  * Use a `SnowGesture.GestureListener` to register a gesture and it accompanying listener.
  * Use a `SnowGesture.GestureCompleteListener` to implement the recognition yourself.

## Gesture string ##

A gesture string consists of characters which indicate a direction: U for up, D for down, R for right and L for left. The gesture string should be in upper case, but the `registerGesture()` converts it to upper case in case you didn't already.
An example:
```
String gestureString = "LRU";
```
Means first go Left then Right and then Up.
In case the gesture string contains unknow direction characters, the `registerGesture()` will return false.

## implementation ##

This is a very simple (yet working :) ) example:
```
public class SnowGestureTest extends Activity {
	private static final String TAG = "SnowGestureTest";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        txtGesture = (EditText)findViewById(R.id.txtGesture);
        
        mGesture = new SnowGesture();
        mGesture.registerGestureCompleteListener(new SnowGesture.GestureCompleteListener() {
		@Override
		public void onGestureCompleted(String gesture) {
			// This will be invoked when the user makes a random gesture
			SnowGestureCreator.this.txtGesture.setText(gesture);
		}
        });
        
        mGesture.registerGesture("RLUL", new SnowGesture.GestureListener() {
		@Override
		public void onRecognized() {
			// This will be invoked when the user gestures
			// Right, Left, Up and Left
			SnowGestureCreator.this.txtGesture.setText("RLUL");
		}
        });
        
        mGesture.registerGesture("UDL", new SnowGesture.GestureListener() {
		@Override
		public void onRecognized() {
			// This will be invoked when the user gestures
			// Up, Down and Left
			SnowGestureCreator.this.txtGesture.setText("UDL");
		}
        });
    }    
    
    @Override
	public boolean onTouchEvent(MotionEvent event) {
        	mGesture.touchEvent(event);

		return super.onTouchEvent(event);
	}
    
    private EditText txtGesture;
    private SnowGesture mGesture;
}
```

This doesn't do much, but it should make clear how to use the SnowGesture class.

If you have any questions left, look through the docs of the class itself, or leave a comment.

# Character Recognizer #

Just for fun I implemented a simple character recognizer, which recognizes the 26 letter is the alphabet and the '?' (for the video :)). This is just a Proof of concept as it's a really crude recognition. The code is in the svn! Check it out.

http://www.youtube.com/watch?v=QrrqxQpAapk