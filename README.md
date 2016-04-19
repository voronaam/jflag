jflag
=====

Test project to modify JVM flags in runtime

This project is set up as a multi-build with one-elf repository. I am not taking that one extra step in combining the two repositories just as a safe guard.

This code allows one to modify JVM heap (C-heap, not Java-heap) in runtime to set flags. That is quite dangerous and because of that I do not want this code to be readiliy useful. The person should have some JVM internals understanding to use this code.

The code in this repository does not do anything of a particular interest. It sets couple of mutually exclusive flags and turns on one of the tracing options. That on itself does not cause JVM to crash. But with only a few modification it is quite easy to kill JVM.

The code is based on [article](http://habrahabr.ru/company/odnoklassniki/blog/195004/) by Andrey.


To build and run this project clone one-elf inside this project and run

```
gradle build && java -cp build/classes/main:one-elf/build/classes/main ca.vorona.jflag.Runner
```

Note that it is not made too easy to run on purpose.
