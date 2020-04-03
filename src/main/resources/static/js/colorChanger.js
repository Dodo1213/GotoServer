var defaultBackground = '#2196F3';
var defaultColor = '#bce0fb';

var defaultOtherBackground = '#919696';
var defaultOtherColor = '#DBF0FF';

let buttonElements = document.getElementsByTagName("button");
for (var i = 0; i < buttonElements.length; i++) {
    var loopElement = buttonElements.item(i);
    if (loopElement.getAttribute("id") != null && loopElement.getAttribute("id") === 'other') {
        loopElement.style.background = defaultOtherBackground;
        loopElement.style.color = defaultOtherColor;

        loopElement.onmouseover = function() {
            this.style.background = '#ABB6B6';
            this.style.color = 'white';
        };

        loopElement.onmouseout = function() {
            this.style.background = defaultOtherBackground;
            this.style.color = defaultOtherColor;
        };
        continue;
    }

    loopElement.style.background = defaultBackground;
    loopElement.style.color = defaultColor;
}