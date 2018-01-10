package net.yageek.strasbourgpark.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.yageek.strasbourgpark.R;

/**
 * Created by yheinrich on 08.01.18.
 */

public class LoadingView extends RelativeLayout {

    public ProgressBar progressBar;
    public TextView textView;

    public LoadingView(Context context) {
        super(context);
        init(context);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.loading_view, this);

        progressBar = findViewById(R.id.loading_view_progressbar);
        textView = findViewById(R.id.loading_view_text);
    }
}
