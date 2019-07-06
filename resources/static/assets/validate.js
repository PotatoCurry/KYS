var green = "#4CAF50";
var red = "#D32F2F";

function check(e) {
    var x = e.value !== "";
    e.style.borderColor = x ? green : red;
}

function checkNum(e) {
    var x = e.value > 99999 && e.value < 1000000; // is valid id
    e.style.borderColor = x ? green : red;
}
