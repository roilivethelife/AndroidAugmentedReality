package com.example.roi.climaar.modelo.figuras.Texto;
// This is a OpenGL ES 1.0 dynamic font rendering system. It loads actual font
// files, generates a font map (texture) from them, and allows rendering of
// text strings.
//
// NOTE: the rendering portions of this class uses a sprite batcher in order
// provide decent speed rendering. Also, rendering assumes a BOTTOM-LEFT
// origin, and the (x,y) positions are relative to that, as well as the
// bottom-left of the string to render.

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.example.roi.climaar.modelo.figuras.Textura;

public class GLTextDrawer {

    private static final String FONT_DEFAULT = "Roboto-Regular.ttf";
    private static final int SIZE_DEFAULT = 100;


    private static GLTextDrawer instance = new GLTextDrawer();

    private boolean isLoaded;

    //--Constants--//
    public final static int CHAR_START = 32;           // First Character (ASCII Code)
    public final static int CHAR_END = 126;            // Last Character (ASCII Code)
    public final static int CHAR_CNT = ( ( ( CHAR_END - CHAR_START ) + 1 ) + 1 );  // Character Count (Including Character to use for Unknown)

    public final static int CHAR_NONE = 32;            // Character to Use for Unknown (ASCII Code)
    public final static int CHAR_UNKNOWN = ( CHAR_CNT - 1 );  // Index of the Unknown Character

    public final static int FONT_SIZE_MIN = 6;         // Minumum Font Size (Pixels)
    public final static int FONT_SIZE_MAX = 180;       // Maximum Font Size (Pixels)
    private static final int CHAR_LF = '\n'-CHAR_START;
    private static final float SCALE_F = 0.1f;

    //--Members--//
    //AssetManager assets;                               // Asset Manager

    int fontPadX, fontPadY;                            // Font Padding (Pixels; On Each Side, ie. Doubled on Both X+Y Axis)

    float fontHeight;                                  // Font Height (Actual; Pixels)
    float fontAscent;                                  // Font Ascent (Above Baseline; Pixels)
    float fontDescent;                                 // Font Descent (Below Baseline; Pixels)

    Textura texture;
    int textureSize;                                   // Texture Size for Font (Square) [NOTE: Public for Testing Purposes Only!]
    TextureRegion textureRgn;                          // Full Texture Region

    float charWidthMax;                                // Character Width (Maximum; Pixels)
    float charHeight;                                  // Character Height (Maximum; Pixels)
    final float[] charWidths;                          // Width of Each Character (Actual; Pixels)
    TextureRegion[] charRgn;                           // Region of Each Character (Texture Coordinates)
    int cellWidth, cellHeight;                         // Character Cell Width/Height
    int rowCnt, colCnt;                                // Number of Rows/Columns

    float scaleX, scaleY;                              // Font Scale (X,Y Axis)
    float spaceY, spaceX;                              // Additional (X,Y Axis) Spacing (Unscaled)


    //--Constructor--//
    // D: save GL instance + asset manager, create arrays, and initialize the members
    private GLTextDrawer() {
        charWidths = new float[CHAR_CNT];               // Create the Array of Character Widths
        charRgn = new TextureRegion[CHAR_CNT];          // Create the Array of Character Regions

        // initialize remaining members
        fontPadX = 0;
        fontPadY = 0;

        fontHeight = 0.0f;
        fontAscent = 0.0f;
        fontDescent = 0.0f;

        textureSize = 0;

        charWidthMax = 0;
        charHeight = 0;

        cellWidth = 0;
        cellHeight = 0;
        rowCnt = 0;
        colCnt = 0;

        scaleX = SCALE_F;                                  // Default Scale = .1 (Unscaled)
        scaleY = SCALE_F;                                  // Default Scale = .1 (Unscaled)
        spaceX = 0.0f;
        spaceY = 0.0f;
        isLoaded = false;
    }


    public static GLTextDrawer loadText(AssetManager assets){
        if(instance.isLoaded){
            return instance;
        }else{
            if(instance.load(assets,FONT_DEFAULT,SIZE_DEFAULT,0,0)){
                return instance;
            }else{
                return null;
            }
        }
    }

    public static void reloadTextDrawer(){
        if(instance.isLoaded){
            instance.isLoaded=false;
        }
    }


    //--Load Font--//
    // description
    //    this will load the specified font file, create a texture for the defined
    //    character range, and setup all required values used to render with it.
    // arguments:
    //    file - Filename of the font (.ttf, .otf) to use. In 'Assets' folder.
    //    size - Requested pixel size of font (height)
    //    padX, padY - Extra padding per character (X+Y Axis); to prevent overlapping characters.
    private boolean load(AssetManager assets, String file, int size, int padX, int padY) {

        // setup requested values
        fontPadX = padX;                                // Set Requested X Axis Padding
        fontPadY = padY;                                // Set Requested Y Axis Padding

        // load the font and setup paint instance for drawing
        Typeface tf = Typeface.createFromAsset( assets, file );  // Create the Typeface from Font File
        Paint paint = new Paint();                      // Create Android Paint Instance
        paint.setAntiAlias( true );                     // Enable Anti Alias
        paint.setTextSize( size );                      // Set Text Size
        paint.setColor( 0xffffffff );                   // Set ARGB (White, Opaque)
        paint.setTypeface( tf );                        // Set Typeface

        // get font metrics
        Paint.FontMetrics fm = paint.getFontMetrics();  // Get Font Metrics
        fontHeight = (float)Math.ceil( Math.abs( fm.bottom ) + Math.abs( fm.top ) );  // Calculate Font Height
        fontAscent = (float)Math.ceil( Math.abs( fm.ascent ) );  // Save Font Ascent
        fontDescent = (float)Math.ceil( Math.abs( fm.descent ) );  // Save Font Descent

        // determine the width of each character (including unknown character)
        // also determine the maximum character width
        char[] s = new char[2];                         // Create Character Array
        charWidthMax = charHeight = 0;                  // Reset Character Width/Height Maximums
        float[] w = new float[2];                       // Working Width Value
        int cnt = 0;                                    // Array Counter
        for ( char c = CHAR_START; c <= CHAR_END; c++ )  {  // FOR Each Character
            s[0] = c;                                    // Set Character
            paint.getTextWidths( s, 0, 1, w );           // Get Character Bounds
            charWidths[cnt] = w[0];                      // Get Width
            if ( charWidths[cnt] > charWidthMax )        // IF Width Larger Than Max Width
                charWidthMax = charWidths[cnt];           // Save New Max Width
            cnt++;                                       // Advance Array Counter
        }
        s[0] = CHAR_NONE;                               // Set Unknown Character
        paint.getTextWidths( s, 0, 1, w );              // Get Character Bounds
        charWidths[cnt] = w[0];                         // Get Width
        if ( charWidths[cnt] > charWidthMax )           // IF Width Larger Than Max Width
            charWidthMax = charWidths[cnt];              // Save New Max Width
        cnt++;                                          // Advance Array Counter

        // set character height to font height
        charHeight = fontHeight;                        // Set Character Height

        // find the maximum size, validate, and setup cell sizes
        cellWidth = (int)charWidthMax + ( 2 * fontPadX );  // Set Cell Width
        cellHeight = (int)charHeight + ( 2 * fontPadY );  // Set Cell Height
        int maxSize = cellWidth > cellHeight ? cellWidth : cellHeight;  // Save Max Size (Width/Height)
        if ( maxSize < FONT_SIZE_MIN || maxSize > FONT_SIZE_MAX )  // IF Maximum Size Outside Valid Bounds
            return false;                                // Return Error

        // set texture size based on max font size (width or height)
        // NOTE: these values are fixed, based on the defined characters. when
        // changing start/end characters (CHAR_START/CHAR_END) this will need adjustment too!
        if ( maxSize <= 24 )                            // IF Max Size is 18 or Less
            textureSize = 256;                           // Set 256 Texture Size
        else if ( maxSize <= 40 )                       // ELSE IF Max Size is 40 or Less
            textureSize = 512;                           // Set 512 Texture Size
        else if ( maxSize <= 80 )                       // ELSE IF Max Size is 80 or Less
            textureSize = 1024;                          // Set 1024 Texture Size
        else                                            // ELSE IF Max Size is Larger Than 80 (and Less than FONT_SIZE_MAX)
            textureSize = 2048;                          // Set 2048 Texture Size

        // create an empty bitmap (alpha only)
        Bitmap bitmap = Bitmap.createBitmap( textureSize, textureSize, Bitmap.Config.ARGB_8888 );  // Create Bitmap
        Canvas canvas = new Canvas( bitmap );           // Create Canvas for Rendering to Bitmap
        bitmap.eraseColor( 0x00000000 );                // Set Transparent Background (ARGB)

        // calculate rows/columns
        // NOTE: while not required for anything, these may be useful to have :)
        colCnt = textureSize / cellWidth;               // Calculate Number of Columns
        rowCnt = (int)Math.ceil( (float)CHAR_CNT / (float)colCnt );  // Calculate Number of Rows

        // render each of the characters to the canvas (ie. build the font map)
        float x = fontPadX;                             // Set Start Position (X)
        float y = ( cellHeight - 1 ) - fontDescent - fontPadY;  // Set Start Position (Y)
        for ( char c = CHAR_START; c <= CHAR_END; c++ )  {  // FOR Each Character
            s[0] = c;                                    // Set Character to Draw
            canvas.drawText( s, 0, 1, x, y, paint );     // Draw Character
            x += cellWidth;                              // Move to Next Character
            if ( ( x + cellWidth - fontPadX ) > textureSize )  {  // IF End of Line Reached
                x = fontPadX;                             // Set X for New Row
                y += cellHeight;                          // Move Down a Row
            }
        }
        s[0] = CHAR_NONE;                               // Set Character to Use for NONE
        canvas.drawText( s, 0, 1, x, y, paint );        // Draw Character

        texture = new Textura(bitmap);
        // release the bitmap
        bitmap.recycle();                               // Release the Bitmap

        // setup the array of character texture regions
        x = 0;                                          // Initialize X
        y = 0;                                          // Initialize Y
        for ( int c = 0; c < CHAR_CNT; c++ )  {         // FOR Each Character (On Texture)
            charRgn[c] = new TextureRegion( textureSize, textureSize, x, y, cellWidth-1, cellHeight-1 );  // Create Region for Character
            x += cellWidth;                              // Move to Next Char (Cell)
            if ( x + cellWidth > textureSize )  {
                x = 0;                                    // Reset X Position to Start
                y += cellHeight;                          // Move to Next Row (Cell)
            }
        }

        // create full texture region
        textureRgn = new TextureRegion( textureSize, textureSize, 0, 0, textureSize, textureSize );  // Create Full Texture Region

        // return success
        isLoaded =  true;                                    // Return Success
        return true;
    }



    //--Draw Text--//
    // D: draw text at the specified x,y position
    // A: text - the string to draw
    //    x, y - the x,y position to draw text at (bottom left of text; including descent)
    // R: [none]
    public void draw(GLText glText, String text){
        draw(glText, text,0,0);
    }
    public void draw(GLText glText, String text, float startX, float startY)  {
        float chrHeight = cellHeight * scaleY;          // Calculate Scaled Character Height

        int len = text.length();                        // Get String Length
        float x = startX;
        float y = startY;

        float[] fVertexData = new float[len*6*8];
        //x += ( chrWidth / 2.0f ) - ( fontPadX * scaleX );  // Adjust Start X
        //y += ( chrHeight / 2.0f ) - ( fontPadY * scaleY );  // Adjust Start Y
        for ( int i = 0; i < len; i++ )  {              // FOR Each Character in String
            int c = (int)text.charAt( i ) - CHAR_START;  // Calculate Character Index (Offset by First Char in Font)
            if ( c < 0 || c >= CHAR_CNT ){// IF Character Not In Font
                if(c==CHAR_LF){
                    x=startX-( charWidths[CHAR_UNKNOWN] + spaceX ) * scaleX;//x=start-1 letra(porque dibujamos UNKNOWN))
                    y-= chrHeight+spaceY;
                }
                c = CHAR_UNKNOWN;                         // Set to Unknown Character Index
            }

            drawLetterHelper(fVertexData,i*6*8,x,y,charWidths[c]*scaleX,chrHeight,charRgn[c]);
            x += ( charWidths[c] + spaceX ) * scaleX;    // Advance X Position by Scaled Character Width
        }
        glText.editText(fVertexData,len,texture);
    }

    //48Floats
    private void drawLetterHelper(float[] fVertexData, int startArray, float x, float y, float width, float height, TextureRegion charRgn){
        for (int i = 0; i <6; i++) {
            fVertexData[8*i+startArray+2]=0.0f;//z
            fVertexData[8*i+startArray+3]=0.0f;//norm_x
            fVertexData[8*i+startArray+4]=0.0f;//norm_y
            fVertexData[8*i+startArray+5]=1.0f;//norm_z
        }
        //B-
        fVertexData[startArray]=x;
        fVertexData[startArray+1]=y;
        fVertexData[startArray+6]=charRgn.u1;
        fVertexData[startArray+7]=charRgn.v2;
        //C-
        fVertexData[8+startArray]=x+width;
        fVertexData[8+startArray+1]=y+height;
        fVertexData[8+startArray+6]=charRgn.u2;
        fVertexData[8+startArray+7]=charRgn.v1;
        //A-
        fVertexData[16+startArray]=x;
        fVertexData[16+startArray+1]=y+height;
        fVertexData[16+startArray+6]=charRgn.u1;
        fVertexData[16+startArray+7]=charRgn.v1;
        //B-
        fVertexData[24+startArray]=x;
        fVertexData[24+startArray+1]=y;
        fVertexData[24+startArray+6]=charRgn.u1;
        fVertexData[24+startArray+7]=charRgn.v2;
        //D-
        fVertexData[32+startArray]=x+width;
        fVertexData[32+startArray+1]=y;
        fVertexData[32+startArray+6]=charRgn.u2;
        fVertexData[32+startArray+7]=charRgn.v2;
        //C-
        fVertexData[40+startArray]=x+width;
        fVertexData[40+startArray+1]=y+height;
        fVertexData[40+startArray+6]=charRgn.u2;
        fVertexData[40+startArray+7]=charRgn.v1;
    }

    //--Draw Text Centered--//
    // D: draw text CENTERED at the specified x,y position
    // A: text - the string to draw
    //    x, y - the x,y position to draw text at (bottom left of text)
    // R: the total width of the text that was drawn
    public float drawC(GLText glText,String text, float x, float y)  {
        float len = getLength( text );                  // Get Text Length
        float height = getHeight(text);
        draw(glText, text, x - ( len / 2.0f ), y + ( height / 2.0f ));  // Draw Text Centered
        return len;                                     // Return Length
    }
    public float drawCX(GLText glText,String text, float x, float y)  {
        float len = getLength( text );                  // Get Text Length
        draw(glText,text, x - ( len / 2.0f ), y );            // Draw Text Centered (X-Axis Only)
        return len;                                     // Return Length
    }
    public void drawCY(GLText glText, String text, float x, float y)  {
        float height = getHeight(text);
        draw(glText, text, x, y + ( height / 2.0f ) );  // Draw Text Centered (Y-Axis Only)
    }

    //--Set Scale--//
    // D: set the scaling to use for the font
    // A: scale - uniform scale for both x and y axis scaling
    //    sx, sy - separate x and y axis scaling factors
    // R: [none]
    public void setScale(float scale)  {
        scaleX = scaleY = scale*SCALE_F;                        // Set Uniform Scale
    }
    public void setScale(float sx, float sy)  {
        scaleX = sx*SCALE_F;                                    // Set X Scale
        scaleY = sy*SCALE_F;                                    // Set Y Scale
    }

    //--Get Scale--//
    // D: get the current scaling used for the font
    // A: [none]
    // R: the x/y scale currently used for scale
    public float getScaleX()  {
        return scaleX/SCALE_F;                                  // Return X Scale
    }
    public float getScaleY()  {
        return scaleY/SCALE_F;                                  // Return Y Scale
    }

    //--Set Space--//
    // D: set the spacing (unscaled; ie. pixel size) to use for the font
    // A: space - space for x axis spacing
    // R: [none]
    public void setSpace(float space)  {
        spaceX = space;                                 // Set Space
    }

    //--Get Space--//
    // D: get the current spacing used for the font
    // A: [none]
    // R: the x/y space currently used for scale
    public float getSpace()  {
        return spaceX;                                  // Return X Space
    }

    //--Get Length of a String--//
    // D: return the length of the specified string if rendered using current settings
    // A: text - the string to get length for
    // R: the length of the specified string (pixels)
    public float getLength(String text) {
        float len = 0.0f;                               // Working Length
        float maxLen = 0.0f;                               // Working Length
        int strLen = text.length();                     // Get String Length (Characters)
        for ( int i = 0; i < strLen; i++ )  {           // For Each Character in String (Except Last
            int c = (int)text.charAt( i ) - CHAR_START;  // Calculate Character Index (Offset by First Char in Font)
            if ( c < 0 || c >= CHAR_CNT ){// IF Character Not In Font
                if(c==CHAR_LF){
                    if(len>maxLen) {
                        maxLen = len;
                    }
                    len = 0f;
                }
            }else {
                len += (charWidths[c] * scaleX);           // Add Scaled Character Width to Total Length
            }
        }
        if(len>maxLen) {
            maxLen = len;
        }
        maxLen += ( strLen > 1 ? ( ( strLen - 1 ) * spaceX ) * scaleX : 0 );  // Add Space Length
        return maxLen;                                     // Return Total Length
    }

    public float getHeight(String text){
        float charHeight = getCharHeight();
        float len = charHeight;
        int strLen = text.length();                     // Get String Length (Characters)
        for ( int i = 0; i < strLen; i++ ) {
            if(text.charAt(i)=='\n'){
                len+= charHeight+spaceY;
            }
        }
        return  len;
    }

    //--Get Width/Height of Character--//
    // D: return the scaled width/height of a character, or max character width
    //    NOTE: since all characters are the same height, no character index is required!
    //    NOTE: excludes spacing!!
    // A: chr - the character to get width for
    // R: the requested character size (scaled)
    public float getCharWidth(char chr)  {
        int c = chr - CHAR_START;                       // Calculate Character Index (Offset by First Char in Font)
        return ( charWidths[c] * scaleX );              // Return Scaled Character Width
    }
    public float getCharWidthMax()  {
        return ( charWidthMax * scaleX );               // Return Scaled Max Character Width
    }
    public float getCharHeight() {
        return ( charHeight * scaleY );                 // Return Scaled Character Height
    }

    //--Get Font Metrics--//
    // D: return the specified (scaled) font metric
    // A: [none]
    // R: the requested font metric (scaled)
    public float getAscent()  {
        return ( fontAscent * scaleY );                 // Return Font Ascent
    }
    public float getDescent()  {
        return ( fontDescent * scaleY );                // Return Font Descent
    }
    /*public float getHeight()  {
        return ( fontHeight * scaleY );                 // Return Font Height (Actual)
    }*/


    //--Draw Font Texture--//
    // D: draw the entire font texture (NOTE: for testing purposes only)
    // A: width, height - the width and height of the area to draw to. this is used
    //    to draw the texture to the top-left corner.
    public void drawTexture(GLText glText,int width, int height)  {
        float[] fVertexData = new float[6*8];
        TextureRegion textureRegion = new TextureRegion(textureSize,textureSize,0,0,textureSize,textureSize);
        drawLetterHelper(fVertexData,0,0,0,width,height,textureRegion);
        glText.editText(fVertexData,1,texture);
    }


}