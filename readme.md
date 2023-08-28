Raymond Chen
20790117 r276chen
macOS 11.5.2 (MacBook Pro 2018)
Kotlin 1.5
Android SDK API 31
Android Studio 2021.3

<div>Trashcan icon <a href="https://www.freepik.com" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a></div>

Instructions:
 - In the Addition page, click "ADD" button  to store a gesture in the library
 - In the Library page, click trashcan icon  to remove the gesture in the library
 - In the Recognition page, click "OK" button to match the gesture on the canvas

Note:
1. adding a gesture whose name already exist in the library will replace the existing one without any alerts.
2. when the user try to draw the path where part of the path is outside of the canvas, the point where tha path leave the canvas will be connected with the point where the point enters back the canvas
3. when the user try to draw a path that ends outside the canvas, the path will be cut short at the bounds of the canvas.