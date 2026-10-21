package com.smartpantry.manager.views;

import android.animation.ValueAnimator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/** A glass cookie jar whose pastel fill rises with the displayed count. */
public class CookieJarStatView extends View {
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path body=new Path();
    private int value;
    private int accent=Color.rgb(40,122,80);
    private String label="Pantry items";
    private float fillFraction;
    private ValueAnimator fillAnimator;

    public CookieJarStatView(Context context, AttributeSet attrs) {
        super(context,attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        setClickable(true);
        setOnClickListener(view->ObjectAnimator.ofFloat(this,"rotation",0f,-7f,6f,-4f,3f,0f).setDuration(520).start());
    }

    public void setStat(int value,String label,int accent) {
        this.value=Math.max(0,value); this.label=label; this.accent=accent;
        setContentDescription(value+" "+label);
        float target=value==0?0f:Math.min(.86f,value/(value+5f));
        if(fillAnimator!=null) fillAnimator.cancel();
        fillAnimator=ValueAnimator.ofFloat(fillFraction,target);
        fillAnimator.setDuration(550);
        fillAnimator.addUpdateListener(animation->{ fillFraction=(float)animation.getAnimatedValue(); invalidate(); });
        fillAnimator.start();
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w=getWidth(), h=getHeight();
        if(w<=0||h<=0) return;
        float scale=Math.min(w/170f,h/192f);
        canvas.save(); canvas.translate((w-170f*scale)/2f,(h-192f*scale)/2f); canvas.scale(scale,scale);

        // A small shadow, glass shoulders and a broad rounded base make the silhouette jar-like.
        paint.setStyle(Paint.Style.FILL); paint.setColor(0x1F31533D);
        canvas.drawOval(new RectF(24,177,146,188),paint);
        body.reset(); body.moveTo(43,42); body.lineTo(127,42);
        body.cubicTo(128,52,139,56,142,71);
        body.lineTo(146,155); body.cubicTo(147,169,138,177,126,177);
        body.lineTo(44,177); body.cubicTo(32,177,23,169,24,155);
        body.lineTo(28,71); body.cubicTo(31,56,42,52,43,42); body.close();
        paint.setColor(0xFFFDFEFC); canvas.drawPath(body,paint);

        canvas.save(); canvas.clipPath(body);
        float top=173f-fillFraction*120f;
        paint.setColor(withAlpha(accent,36));
        canvas.drawRect(22,top,148,180,paint);
        paint.setColor(withAlpha(accent,55));
        canvas.drawOval(new RectF(22,top-7,148,top+7),paint);
        paint.setColor(0x80FFFFFF);
        canvas.drawRoundRect(new RectF(36,66,43,146),4,4,paint);
        canvas.restore();

        paint.setStyle(Paint.Style.STROKE); paint.setStrokeWidth(2.4f); paint.setColor(withAlpha(accent,210));
        canvas.drawPath(body,paint); paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFFFCFCF8); canvas.drawRoundRect(new RectF(36,31,134,48),7,7,paint);
        paint.setStyle(Paint.Style.STROKE); paint.setStrokeWidth(2.3f); paint.setColor(accent);
        canvas.drawRoundRect(new RectF(36,31,134,48),7,7,paint);
        paint.setStyle(Paint.Style.FILL); paint.setColor(withAlpha(accent,85));
        canvas.drawRoundRect(new RectF(67,22,103,32),5,5,paint);
        paint.setColor(0xFFFCFCF8); canvas.drawOval(new RectF(48,162,122,172),paint);

        paint.setTextAlign(Paint.Align.CENTER); paint.setTypeface(android.graphics.Typeface.create("sans-serif-rounded",android.graphics.Typeface.BOLD));
        paint.setColor(accent); paint.setTextSize(value>999?36:44);
        canvas.drawText(String.valueOf(value),85,112,paint);
        paint.setTypeface(android.graphics.Typeface.create("sans-serif-medium",android.graphics.Typeface.NORMAL));
        paint.setColor(0xE8FDFEFC);
        canvas.drawRoundRect(new RectF(31,128,139,158),11,11,paint);
        paint.setTextSize(label.length()>16?12.5f:14f); paint.setColor(0xFF26332B);
        canvas.drawText(label,85,149,paint);
        canvas.restore();
    }

    private static int withAlpha(int color,int alpha) { return (color&0x00FFFFFF)|(alpha<<24); }
    @Override protected void onDetachedFromWindow() { if(fillAnimator!=null) fillAnimator.cancel(); super.onDetachedFromWindow(); }
}
