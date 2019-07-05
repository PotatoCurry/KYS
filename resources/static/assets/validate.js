var green = "#4CAF50";
<<<<<<< HEAD
var red = "#D32F2F"
=======

>>>>>>> 158dfb1... uh i hope this doesnt break anything

function check(e){
    var x = e.value != "";
    if(x){
        e.style.borderColor = green;
    }
    else{
<<<<<<< HEAD
        e.style.borderColor = red;
=======
        e.style.borderColor = "red";
>>>>>>> 158dfb1... uh i hope this doesnt break anything
    }
}

function checknum(e){
    var x = e.value > 99999 && e.value <1000000; // is valid id
    if(x){
        e.style.borderColor = green;
    }
    else{
<<<<<<< HEAD
        e.style.borderColor = red;
=======
        e.style.borderColor = "red";
>>>>>>> 158dfb1... uh i hope this doesnt break anything
    }
    
}