var green = "#4CAF50";
<<<<<<< HEAD
<<<<<<< HEAD
var red = "#D32F2F"
=======

>>>>>>> 158dfb1... uh i hope this doesnt break anything
=======
var red = "#D32F2F"
>>>>>>> 3398675... yeselite page

function check(e){
    var x = e.value != "";
    if(x){
        e.style.borderColor = green;
    }
    else{
<<<<<<< HEAD
<<<<<<< HEAD
        e.style.borderColor = red;
=======
        e.style.borderColor = "red";
>>>>>>> 158dfb1... uh i hope this doesnt break anything
=======
        e.style.borderColor = red;
>>>>>>> 3398675... yeselite page
    }
}

function checknum(e){
    var x = e.value > 99999 && e.value <1000000; // is valid id
    if(x){
        e.style.borderColor = green;
    }
    else{
<<<<<<< HEAD
<<<<<<< HEAD
        e.style.borderColor = red;
=======
        e.style.borderColor = "red";
>>>>>>> 158dfb1... uh i hope this doesnt break anything
=======
        e.style.borderColor = red;
>>>>>>> 3398675... yeselite page
    }
    
}