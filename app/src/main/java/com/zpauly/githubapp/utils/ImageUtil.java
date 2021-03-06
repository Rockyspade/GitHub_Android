/*
 * Copyright 2012 GitHub Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zpauly.githubapp.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.zpauly.githubapp.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

/**
 * Created by zpauly on 16-8-1.
 */
public class ImageUtil {

    private static final String TAG = "ImageUtils";

    public static Bitmap getBitmap(final String imagePath, int sampleSize) {
        final Options options = new Options();
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inSampleSize = sampleSize;

        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(imagePath, "r");
            return BitmapFactory.decodeFileDescriptor(file.getFD(), null,
                    options);
        } catch (IOException e) {
            Log.d(TAG, e.getMessage(), e);
            return null;
        } finally {
            if (file != null)
                try {
                    file.close();
                } catch (IOException e) {
                    Log.d(TAG, e.getMessage(), e);
                }
        }
    }

    public static Point getSize(final String imagePath) {
        final Options options = new Options();
        options.inJustDecodeBounds = true;

        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(imagePath, "r");
            BitmapFactory.decodeFileDescriptor(file.getFD(), null, options);
            return new Point(options.outWidth, options.outHeight);
        } catch (IOException e) {
            Log.d(TAG, e.getMessage(), e);
            return null;
        } finally {
            if (file != null)
                try {
                    file.close();
                } catch (IOException e) {
                    Log.d(TAG, e.getMessage(), e);
                }
        }
    }

    public static Bitmap getBitmap(final File image, int width, int height) {
        String imagePath = image.getAbsolutePath();
        Point size = getSize(imagePath);
        if (size == null) {
            return null;
        }

        int currWidth = size.x;
        int currHeight = size.y;

        int scale = 1;
        while (currWidth >= width || currHeight >= height) {
            currWidth /= 2;
            currHeight /= 2;
            scale *= 2;
        }

        return getBitmap(imagePath, scale);
    }

    public static Bitmap renderSvgToBitmap(Resources res, InputStream is,
                                           int maxWidth, int maxHeight) {
        try {
            SVG svg = SVG.getFromInputStream(is);
            if (svg != null) {
                svg.setRenderDPI(DisplayMetrics.DENSITY_DEFAULT);
                Float density = res.getDisplayMetrics().density;
                int docWidth = (int) (svg.getDocumentWidth() * density);
                int docHeight = (int) (svg.getDocumentHeight() * density);
                if (docWidth < 0 || docHeight < 0) {
                    float aspectRatio = svg.getDocumentAspectRatio();
                    if (aspectRatio > 0) {
                        float heightForAspect = (float) maxWidth / aspectRatio;
                        float widthForAspect = (float) maxHeight * aspectRatio;
                        if (widthForAspect < heightForAspect) {
                            docWidth = Math.round(widthForAspect);
                            docHeight = maxHeight;
                        } else {
                            docWidth = maxWidth;
                            docHeight = Math.round(heightForAspect);
                        }
                    } else {
                        docWidth = maxWidth;
                        docHeight = maxHeight;
                    }

                    // we didn't take density into account anymore when calculating docWidth
                    // and docHeight, so don't scale with it and just let the renderer
                    // figure out the scaling
                    density = null;
                }

                while (docWidth >= maxWidth || docHeight >= maxHeight) {
                    docWidth /= 2;
                    docHeight /= 2;
                    if (density != null) {
                        density /= 2;
                    }
                }

                Bitmap bitmap = Bitmap.createBitmap(docWidth, docHeight, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                if (density != null) {
                    canvas.scale(density, density);
                }
                svg.renderToCanvas(canvas);
                return bitmap;
            }
        } catch (SVGParseException e) {
            // fall through
        } catch (NullPointerException e) {
            // https://github.com/BigBadaboom/androidsvg/issues/81
            // remove me when there's a 1.2.3 release
        }
        return null;
    }

    public static void loadAvatarImageFromUrl(Context context, String url, ImageView imageView) {
        if (context == null)
            return;
        Glide.with(context)
                .load(Uri.parse(url))
                .placeholder(R.mipmap.avatar)
                .error(R.mipmap.avatar)
                .transform(new CircleTransform(context))
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .crossFade()
                .into(imageView);
    }

    public static void loadAvatarImageFromUrl(Fragment fragment, String url, ImageView imageView) {
        if (fragment == null) {
            return;
        }
        if (!fragment.isDetached()) {
            Glide.with(fragment)
                    .load(Uri.parse(url))
                    .placeholder(R.mipmap.avatar)
                    .error(R.mipmap.avatar)
                    .transform(new CircleTransform(fragment.getContext()))
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .crossFade()
                    .into(imageView);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void loadAvatarImageFromUrl(Activity activity, String url, ImageView imageView) {
        if (activity == null) {
            Log.i(TAG, "context is null");
            return;
        }
        if (!activity.isDestroyed()) {
            Glide.with(activity)
                    .load(Uri.parse(url))
                    .placeholder(R.mipmap.avatar)
                    .error(R.mipmap.avatar)
                    .transform(new CircleTransform(activity))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .crossFade()
                    .into(imageView);
        }
    }

    public static class CircleTransform extends BitmapTransformation {
        public CircleTransform(Context context) {
            super(context);
        }

        @Override protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return circleCrop(pool, toTransform);
        }

        private static Bitmap circleCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;

            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            // TODO this could be acquired from the pool too
            Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);

            Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            return result;
        }

        @Override public String getId() {
            return getClass().getName();
        }
    }
}