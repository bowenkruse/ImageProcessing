# Assignment ONE - Image Processing
##### Due February 5th, 7:00 pm
1. Fix the reset function in the pulldown menu. I fixed it on mine now, but it's a good practice on learning the code and figuring out what's going on. 
2. Rotate image 90 degrees, odd shaped images should work.......mine did when I went back and looked at. 
3. Turn an image into grayscale and display it, use the "Luminosity" method for grayscale conversion. You can put the grayscale value using the three channel, you don't have to create a new image.
4. Blur the grayscale image, use an average of surrounding pixels to blur the image, you will need a second array so you don't use already blurred pixels in your calculations.  First, call the grayscale method from number three. 
    1. The last thing set the original picture array to your temporary blurred array and call resetPicture()
5. Turn a color image into a grayscale image first and then do a minimum of 3x3 mask to do edge detection. 5x5 will work better and be worth more.    
    1. See notes below
6. Show a histogram of the colors in a separate window
    1. See notes below
7.  Use the values in the histogram to equalize the image:
    1.Use the mapping function to normalize the distribution evenly 
    2. https://en.wikipedia.org/wiki/Histogram_equalization 
Track a colored object.....orange is easiest. Result is a binary image that is black except where the colored object is located.
    1. See notes below (I'll also cover this next Wednesday)
