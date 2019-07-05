var green = "#4CAF50";
var red = "#D32F2F"

function check(e){
    var x = e.value != "";
    if(x){
        e.style.borderColor = green;
    }
    else{
        e.style.borderColor = red;
    }
}

function checkNum(e) {
    var x = e.value > 99999 && e.value <1000000; // is valid id
    if(x){
        e.style.borderColor = green;
    }
    else{
        e.style.borderColor = red;
    }

}
