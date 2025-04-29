import cv2
import apriltag
from detect_apriltag import detect_apriltag

try:
    vid = cv2.VideoCapture(2)
except:
    print("Invalid video input")
    exit(0)


vid.set(3,1080)
vid.set(4,920)

while(True):
    #inside infinity loop
    rect,frame = vid.read()
    cv2.imshow('frame',detect_apriltag(frame))

    # image processing code here
    
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

vid.release()
# Destroy all the windows
cv2.destroyAllWindows()


# def draw_tag():


#     return 