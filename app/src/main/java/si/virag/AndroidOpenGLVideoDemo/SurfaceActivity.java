package si.virag.AndroidOpenGLVideoDemo;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;

import com.smallplanet.videoplayer.R;

import java.io.IOException;

import si.virag.AndroidOpenGLVideoDemo.gl.VideoTextureRenderer;

public class SurfaceActivity extends Activity implements TextureView.SurfaceTextureListener {
    private static final String LOG_TAG = "SurfaceTest";

    private TextureView mTextureView;
    private MediaPlayer player;
    private VideoTextureRenderer renderer;

    private int surfaceWidth;
    private int surfaceHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mTextureView = (TextureView) findViewById(R.id.surface);
        mTextureView.setSurfaceTextureListener(this);
//        mTextureView.setScaleX(0.5f);
//        mTextureView.setScaleY(0.5f);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mTextureView.isAvailable())
            startPlayingImpl(mTextureView.getSurfaceTexture());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null)
            player.release();
        if (renderer != null)
            renderer.onPause();
    }

    private void startPlayingImpl(SurfaceTexture sf) {
        renderer = new VideoTextureRenderer(this, sf, surfaceWidth, surfaceHeight);
        player = new MediaPlayer();

        try {
//            AssetFileDescriptor afd = getAssets().openFd("big_buck_bunny.mp4");
            AssetFileDescriptor afd = getAssets().openFd("sync_party_setup_welcome.mp4");
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            renderer.start(new Runnable() {
                @Override
                public void run() {
                    player.setSurface(new Surface(renderer.getVideoTexture()));
                    player.setLooping(true);
                    try {
                        renderer.setVideoSize(player.getVideoWidth(), player.getVideoHeight());
                        player.prepare();
                        player.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException("Could not open input video!");
                    }
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException("Could not open input video!");
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        surfaceWidth = width;
        surfaceHeight = height;
        startPlayingImpl(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
