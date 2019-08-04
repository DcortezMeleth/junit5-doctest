package com.hexagon.doctest.annotations

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DocTest(vararg val examples: String)