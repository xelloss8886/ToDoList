var complete = document.getElementsByClassName("complete");
var i;
for (i = 0; i < complete.length; i++) {
    complete[i].onclick = function() {
        var div = this.parentElement;
        div.style.display = "none";
    }
}

var modify = document.getElementsByClassName("modify");
var i;
for (i = 0; i < modify.length; i++) {
    modify[i].onclick = function() {
        var div = this.parentElement;
        div.style.display = "none";
    }
}



