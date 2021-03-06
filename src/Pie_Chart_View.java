package ahp.thesis.code;

import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Class to create the pie chart diagram in the last step of the application. 
 * @author xaris08
 *
 */
public class Pie_Chart_View extends View {
	private static final int WAIT = 0;
	private static final int IS_READY_TO_DRAW = 1;
	private static final int IS_DRAW = 2;
	private static final float START_INC = 30;
	private Paint mBgPaints   = new Paint();
	private Paint mLinePaints = new Paint();
	private int   mWidth;
	private int   mHeight;
	private int   mGapLeft;
	private int   mGapRight;
	private int   mGapTop;
	private int   mGapBottom;
	private int   mBgColor;
	private int   mState = WAIT;
	private float mStart;
	private float mSweep;
	private int   mMaxConnection;
	private List<Pie_Item> mDataArray;
	//--------------------------------------------------------------------------------------
	public Pie_Chart_View (Context context){
		super(context);
	}
	//--------------------------------------------------------------------------------------
	public Pie_Chart_View(Context context, AttributeSet attrs) {
        super(context, attrs);
	}
    //--------------------------------------------------------------------------------------
    @Override 
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);
    	//------------------------------------------------------
    	if (mState != IS_READY_TO_DRAW) return;
    	canvas.drawColor(mBgColor);
    	//------------------------------------------------------
    	mBgPaints.setAntiAlias(true);
    	mBgPaints.setStyle(Paint.Style.FILL);
    	mBgPaints.setColor(0x88FF0000);
    	mBgPaints.setStrokeWidth(0.5f);
    	//------------------------------------------------------
    	mLinePaints.setAntiAlias(true);
    	mLinePaints.setStyle(Paint.Style.STROKE);
    	mLinePaints.setColor(0xff000000);
    	mLinePaints.setStrokeWidth(0.5f);
    	//------------------------------------------------------
    	RectF mOvals = new RectF( mGapLeft, mGapTop, mWidth - mGapRight, mHeight - mGapBottom);
    	//------------------------------------------------------
    	mStart = START_INC;
    	Pie_Item Item;
    	for (int i = 0; i < mDataArray.size(); i++) {
    		Item = mDataArray.get(i);
    		mBgPaints.setColor(Item.Color);
    		mSweep = 360 * ( Item.Count / mMaxConnection );
    		canvas.drawArc(mOvals, mStart, mSweep, true, mBgPaints);
    		canvas.drawArc(mOvals, mStart, mSweep, true, mLinePaints);
    		mStart += mSweep;
        }
    	//------------------------------------------------------
    	Options options = new BitmapFactory.Options();
        options.inScaled = false;
    	//------------------------------------------------------
    	mState = IS_DRAW;
    }
    //--------------------------------------------------------------------------------------
    public void setGeometry(int width, int height, int GapLeft, int GapRight, int GapTop, int GapBottom) {
    	mWidth     = width;
   	 	mHeight    = height;
   	 	mGapLeft   = GapLeft;
   	 	mGapRight  = GapRight;
   	 	mGapTop    = GapTop;
   	 	mGapBottom = GapBottom;
    }
    //--------------------------------------------------------------------------------------
    public void setSkinParams(int bgColor) {
   	 	mBgColor   = bgColor;
    }
    //--------------------------------------------------------------------------------------
    public void setData(List<Pie_Item> data, int MaxConnection) {
    	mDataArray = data;
    	mMaxConnection = MaxConnection;
    	mState = IS_READY_TO_DRAW;
    }
    //--------------------------------------------------------------------------------------
    public void setState(int State) {
    	mState = State;
    }
    //--------------------------------------------------------------------------------------
    public int getColorValue( int Index ) {
   	 	if (mDataArray == null) return 0;
   	 	if (Index < 0){
   	 		return mDataArray.get(0).Color;
   	 	} else if (Index >= mDataArray.size()){
   	 		return mDataArray.get(mDataArray.size()-1).Color;
   	 	} else {
   	 		return mDataArray.get(mDataArray.size()-1).Color;
   	 	}
    }
}