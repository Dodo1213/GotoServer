let elements = document.getElementsByTagName("label");
for (var i = 0; i < elements.length; i++) {
    elements.item(i).style.top = '0';
    elements.item(i).style.transition = 'none';
}

function onCopyClick() {
    var textToCopy = document.getElementById("url");
    textToCopy.select();
    textToCopy.setSelectionRange(0, 99999); /** Mobile devices **/
    document.execCommand("copy");

    var tooltip = document.getElementById("copyTooltip");
    tooltip.innerHTML = "Link copied successfully";
}