proc addTwo(x, y) {
    x + y
}

proc decorator(function, a, b) {
    function(a, b) + 1
}

print(decorator(addTwo, 1, 2))