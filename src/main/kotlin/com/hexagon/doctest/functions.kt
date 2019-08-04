package com.hexagon.doctest

import com.hexagon.doctest.annotations.DocTest

//@DocTest(
//    "mul(1,2) == 2",
//    "mul(1,2) 2",
//    "mul(3,3) == 9"
//)
fun mul(x: Int, y: Int) : Int {
    return x*y;
}

@DocTest(
    "getTwo() == 2",
    "getTwo() == 1"
)
fun getTwo() = 2