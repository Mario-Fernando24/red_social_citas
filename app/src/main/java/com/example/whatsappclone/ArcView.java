package com.example.whatsappclone;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class ArcView extends View {
    private Paint paint;
    private Path path;

    public ArcView(Context context) {
        super(context);
        init();
    }

    public ArcView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Configurar el pincel
        paint = new Paint();
        paint.setColor(getResources().getColor(android.R.color.holo_blue_dark)); // Cambia el color aquí
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        // Inicializar el Path
        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int arcHeight = 100; // Altura del arco

        // Configurar el arco en la parte inferior
        path.reset();
        path.moveTo(0, 0); // Esquina superior izquierda
        path.lineTo(0, height - arcHeight); // Lado izquierdo hasta antes del arco
        path.quadTo(width / 2, height + arcHeight, width, height - arcHeight); // Arco inferior
        path.lineTo(width, 0); // Lado derecho
        path.close(); // Cerrar el Path

        canvas.drawPath(path, paint);
    }
}
