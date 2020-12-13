# MutableGenerator

MutableGenerator generate class from your interface.

For instance you have an interface:
```
@Mutable
interface Inspection {

    val id: String
    val name: String

}
```

Library will generate class with var fields at the same package
```
open class InspectionMutable(
  override var id: String,
  override var name: String
) : Inspection

fun Inspection.toMutable(): InspectionMutable {
    if (this is InspectionMutable) return this
    return InspectionMutable(this.id, this.name)
}
```

**How to get a Git project into your build:**

**Step 1.** Add the JitPack repository to your build file, add it in your root build.gradle at the end of repositories:
```
allprojects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}
```  
**Step 2.** Add the dependency
```
dependencies {
    implementation 'com.github.R12rus.MutableGenerator:annotations:0.0.3'
    kapt 'com.github.R12rus.MutableGenerator:processor:0.0.3'
}
```
