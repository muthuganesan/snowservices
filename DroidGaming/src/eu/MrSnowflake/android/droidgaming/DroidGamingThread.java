/* GameActivity - This is a game development library for android.
 * Copyright (C) 2008  Maarten 'MrSnowflake' Krijn
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package eu.MrSnowflake.android.droidgaming;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

/**
 * NOTE: Only extend if you know what you're doing.
 * 
 * @author Maarten 'MrSnowflake' Krijn
 * @version %I%, %G%
 */
public class DroidGamingThread extends Thread {
	/**
	 * The events concerning the rendering of a frame.
	 * 
	 * @author Maarten 'MrSnowflake' Krijn
	 */
	public class GameEvent{
		public GameEvent(GameEvent org){
			timeElapsed = org.timeElapsed;
			keysDown = org.keysDown.clone();
			keysHeld = org.keysHeld.clone();
			keysUp = org.keysUp.clone();
		}
		
		public GameEvent(double elapsed) {
			timeElapsed = elapsed;
			keysDown = new boolean[KeyEvent.MAX_KEYCODE + 1];
			keysHeld = new boolean[KeyEvent.MAX_KEYCODE + 1];
			keysUp = new boolean[KeyEvent.MAX_KEYCODE + 1];
		}
		
		public void clearUpDown() {
			keysDown = new boolean[KeyEvent.MAX_KEYCODE + 1];
			keysUp = new boolean[KeyEvent.MAX_KEYCODE + 1];
		}
		
		/** How much time has elapsed since the last frame. */
		public double timeElapsed;
		/** Which keys were pressed down for the first time (not held down!). The index in this array
		 * is KeyEvent.KEYCODE_* .
		 */
		public boolean keysDown[];
		/** Which keys were held down. The index in this array is KeyEvent.KEYCODE_*. */
		public boolean keysHeld[];
		/** Which keys have been let up.. The index in this array is KeyEvent.KEYCODE_* . */
		public boolean keysUp[];
	}

	/**
	 * GameListeners allow you to handle events in the rendering cycle. You should implement this
	 * interface to make games.
	 * @author Maarten 'MrSnowflake' Krijn
	 */
	public interface GameListener {
		/**
		 * Called when the Frame is started. Handle your movements and other stuff here.
		 * 
		 * @param event The GameEvent object containing information about the frame and state of the device.
		 * @return Whether to stop the game loop. Note this will immediately quit the loop, without completing
		 * 		the current frame.
		 */
		public boolean onFrameStart(DroidGamingThread parent, final GameEvent event);
		/**
		 * Called when the Frame ended rendering.
		 * 
		 * @param event The GameEvent object containing information about the frame and state of the device.
		 * @return Whether to stop the game loop. Note this will immediately quit the loop, without completing
		 * 		the current frame.
		 */
		public boolean onFrameEnd(DroidGamingThread parent, final GameEvent event);
		/**
		 * Draw to the canvas inside this function.
		 *
		 * @param canvas The canvas to draw to.
		 * @return Whether to stop the game loop. Note this will immediately quit the loop, without completing
		 * 		the current frame.
		 * TODO Check if this should be removed! 
		 */
		public boolean onRender(DroidGamingThread parent, final Canvas canvas);
	}
	private static final String TAG = "DroidGamingThread";
	
	/**
	 * 
	 * @param context The context from which to load resources
	 * @param holder The surface holder.
	 */
	public DroidGamingThread(Context context, SurfaceHolder holder) {
		mContext = context;
		mSurfaceHolder = holder;
		mListeners = new ArrayList<GameListener>();
		mRunning = false;
		mGameEvent = new GameEvent(System.currentTimeMillis());
	}

	/**
	 * Add a GameListener object to handle the game.
	 * @param listener The GameListener object to add.
	 * @return true if added.
	 */
	public boolean registerGameListener(GameListener listener) {
		return mListeners.add(listener);
	}
	
	/**
	 * Remove a GameListener.
	 * @param listener The GameListener object to remove.
	 * @return true if removed.
	 */
	public boolean removeGameListener(GameListener listener) {
		return mListeners.remove(listener);
	}
	
	@Override
	public void start() {
		mLastTime = System.currentTimeMillis();
		super.start();
	}
	
	public void setSurfaceSize(int width, int height) {
		mCanvasWidth = width;
		mCanvasHeight = height;
	}
	
	public void setRunning(boolean running) {
		mRunning = running;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (!mGameEvent.keysHeld[keyCode])
			mGameEvent.keysDown[keyCode] = true;
		mGameEvent.keysHeld[keyCode] = true;
		
		return true;
	}

	public boolean onKeyMultiple(int keyCode, int count, KeyEvent event) {
		return false;
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		mGameEvent.keysUp[keyCode] = true;
		mGameEvent.keysHeld[keyCode] = false;
		
		return true;
	}

	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

	public boolean onTrackballEvent(MotionEvent event) {
		return true;
	}
	
	public void unPause() {    
		synchronized (mSurfaceHolder) {
			mPaused = false;
            mLastTime = System.currentTimeMillis() + 100;
	    }
	}
	
	public void pause() {    
		synchronized (mSurfaceHolder) {
			mPaused = true;
	    }
	}
	
	@Override
	public void run() {
        while (mRunning) {
        	if (mPaused) {
				try {
					sleep(200);
				} catch (InterruptedException e) {
				}
        	}
				
            Canvas canvas = null;
            try {
                canvas = mSurfaceHolder.lockCanvas(null);
                synchronized (mSurfaceHolder) {
                    long now = System.currentTimeMillis();
                    // Do nothing if mLastTime is in the future.
                    // This allows the game-start to delay the start of the physics
                    // by 100ms or whatever.
                    if (mLastTime > now) 
                    	return;
                    double elapsed = (now - mLastTime) / 1000.0;
                    mLastTime = now;
        			
                    mGameEvent.timeElapsed = elapsed;
                	GameEvent event = new GameEvent(mGameEvent);
                	
        			int numListeners = mListeners.size();
        			for(int i = 0; i < numListeners && mRunning; ++i)
        				mRunning = mListeners.get(i).onFrameStart(this, event);
        			for(int i = 0; i < numListeners && mRunning; ++i)
            			mRunning = mListeners.get(i).onRender(this, canvas);
        			for(int i = 0; i < numListeners && mRunning; ++i)
            			mRunning = mListeners.get(i).onFrameEnd(this, event);
        			mGameEvent.clearUpDown();
                }
            } finally {
                // do this in a finally so that if an exception is thrown
                // during the above, we don't leave the Surface in an
                // inconsistent state
                if (canvas != null) {
                    mSurfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
	}
	
	public int getWidth() {
		return mCanvasWidth;
	}
	
	public int getHeight() {
		return mCanvasHeight;
	}
	
	protected Context mContext;
	protected SurfaceHolder mSurfaceHolder;
	protected int mCanvasWidth;
	protected int mCanvasHeight;
	
	private GameEvent mGameEvent;
	private boolean mPaused;
	private ArrayList<GameListener> mListeners;
	private boolean mRunning;
	private long mLastTime;
}