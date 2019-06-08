package com.example.sev_user.myappt20.helper;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.example.sev_user.myappt20.helper.DeeplabModel;

public class GaussianBlur {

    private static int row;
    private static int col;
    private static int getIndex(int x, int y)
    {
        return (x * col + y);
    }

    private static void printdebug(int [] matrix)
    {
        for (int y = 0; y < row; y++)
        {
            String str = "";
            for (int x = 0; x < col; x++)
            {
                int index = getIndex(y, x);
                int red =  (matrix[index] >> 16) & 0xFF;
                int green = (matrix[index] >> 8) & 0xFF;
                int blue = (matrix[index]) & 0xFF;
                str = str.concat(String.valueOf(red));
                str = str.concat(" ");
                str = str.concat(String.valueOf(green));
                str = str.concat(" ");
                str = str.concat(String.valueOf(blue));
                str = str.concat(" ");
            }
        }
    }

    public static Bitmap blur(Bitmap rawImage, int[] segMatrix, int segWidth, int br)
    {
        col = rawImage.getWidth();
        row = rawImage.getHeight();
        Bitmap output = Bitmap.createBitmap(col, row, Bitmap.Config.ARGB_8888);
        int length = Math.max(row, col);
        int[] wH = new int[length];
        int[] wV = new int[length];
        int[] mIntValues = new int[col * row];
        float resizeRatio = (float) DeeplabModel.INPUT_SIZE / Math.max(col, row);

        rawImage.getPixels(mIntValues, 0, col, 0, 0, col, row );

        for (int c = 0; c < col; c++)
        {
            if (c < br) wH[c] = br + c + 1;
            else if (c >= col - br) wH[c] = col + br - c;
            else wH[c] = 2 * br + 1;
        }

        for (int r = 0; r < row; r++)
        {
            int[][] T = new int[length][3];
            T[0][0] = T[0][1] = T[0][2] = 0;
            for (int c = 0; c < wH[0]; c++)
            {
                int index = getIndex(r, c);
                T[0][0] += (mIntValues[index] >> 16) & 0xFF;
                T[0][1] += (mIntValues[index] >> 8) & 0xFF;
                T[0][2] += (mIntValues[index]) & 0xFF;
            }

            for (int c = 1; c < col; c++)
            {
                int[] L = new int[3];
                int[] R = new int[3];

                if (c - br - 1 < 0)
                    L[0] = L[1] = L[2] = 0;
                else
                {
                    int index = getIndex(r, c - br - 1);
                    L[0] = (mIntValues[index] >> 16) & 0xFF;
                    L[1] = (mIntValues[index] >> 8) & 0xFF;
                    L[2] = (mIntValues[index]) & 0xFF;
                }

                if (c + br >= col)
                    R[0] = R[1] = R[2] = 0;
                else
                {
                    int index = getIndex(r, c + br);
                    R[0] = (mIntValues[index] >> 16) & 0xFF;
                    R[1] = (mIntValues[index] >> 8) & 0xFF;
                    R[2] = (mIntValues[index]) & 0xFF;
                }

                for (int k = 0; k < 3; k++)
                {
                    T[c][k] = T[c - 1][k] - L[k] + R[k];
                }

            }

            for (int c = 1; c < col; c++)
            {
                int red = T[c][0] / wH[c];
                int green = T[c][1] / wH[c];
                int blue = T[c][2] / wH[c];
                int mX = (int) (c * resizeRatio);
                int mY = (int) (r * resizeRatio);
                if (segMatrix[mY * segWidth + mX] == 0)
                    mIntValues[getIndex(r, c)] = Color.rgb(red,green,blue);
            }

        }

        //Vertical Blur

        for (int r = 0; r < row; r++)
        {
            if (r < br) wV[r] = br + r + 1;
            else if (r >= row - br) wV[r] = row + br - r;
            else wV[r] = 2 * br + 1;
        }

        for (int c = 0; c < col; c++)
        {
            int[][] T = new int[length][3];
            T[0][0] = T[0][1] = T[0][2];
            for (int r = 0; r < wV[0]; r++)
            {
                int index = getIndex(r, c);
                T[0][0] += (mIntValues[index] >> 16) & 0xFF;
                T[0][1] += (mIntValues[index] >> 8) & 0xFF;
                T[0][2] += (mIntValues[index]) & 0xFF;
            }

            for (int r = 1; r < row; r++)
            {
                int[] L = new int[3];
                int[] R = new int[3];

                if (r - br - 1 < 0)
                    L[0] = L[1] = L[2] = 0;
                else
                {
                    int index = getIndex(r - br - 1, c);
                    L[0] = (mIntValues[index] >> 16) & 0xFF;
                    L[1] = (mIntValues[index] >> 8) & 0xFF;
                    L[2] = (mIntValues[index]) & 0xFF;
                }

                if (r + br >= row)
                    R[0] = R[1] = R[2] = 0;
                else
                {
                    int index = getIndex(r + br, c);
                    R[0] = (mIntValues[index] >> 16) & 0xFF;
                    R[1] = (mIntValues[index] >> 8) & 0xFF;
                    R[2] = (mIntValues[index]) & 0xFF;
                }

                for (int k = 0; k < 3; k++)
                {
                    T[r][k] = T[r - 1][k] - L[k] + R[k];
                }
            }

            for (int r = 1; r < row; r++)
            {
                int red = T[r][0] / wV[r];
                int green = T[r][1] / wV[r];
                int blue = T[r][2] / wV[r];
                int mX = (int) (c * resizeRatio);
                int mY = (int) (r * resizeRatio);
                if (segMatrix[mY * segWidth + mX] == 0)
                    mIntValues[getIndex(r, c)] = Color.rgb(red,green,blue);
            }
        }

        output.setPixels(mIntValues,0, col, 0, 0, col, row);

        return output;
    }
}
