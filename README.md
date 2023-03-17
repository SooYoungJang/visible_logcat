# Debug_Tool

-----------
## What is Debug_Tool?

Each team (QA team, backend team, front team, etc.) may want to check the logs and data or flows that occur in the app.

This is a library that allows you to visually check data flow and logs in your app.

-----------

## Setup

[![Generic badge](https://img.shields.io/maven-central/v/io.github.snaps-eddy/visibledebugtool)](https://shields.io/)

```
repositories {
  google()
  mavenCentral()
}
``` 

```
dependencies {
  implementation 'io.github.snaps-eddy:visibledebugtool:x.y.z'
}
```

## How to use

#### Balloon supports both Kotlin and Java projects, so you can reference it by your language.

### Create DebugTool with Kotlin

#### We can create an instance of the DebugTool with the DebugTool.Builder class.

```
   val debugTool = DebugTool.Builder(this)
            .setAutoPermissionCheck(true)
            .build()

``` 


### Create DebugTool with Kotlin DSL

#### We can also create an instance of the DebugTool with the Kotlin DSL.

<details>
<summary>Keep reading for more details</summary>

#### You can create an instance of the DebugTool as the following example below:

```
 val debugTool = createDebugTool(this) {
     setAutoPermissionCheck(true)
 }

``` 

</details>



### Create DebugTool with Java

```
    DebugTool debugTool = new DebugTool.Builder(context)    //create 
            .setAutoPermissionCheck(true)
            .build();

``` 

## Show up DebugTool

```
debugTool.bindService()

``` 

## Dismiss DebugTool

```
debugTool.unbindService()

``` 

## Customize
### VisibleDebugTool supports the following methods.

- `setAutoPermissionCheck(Boolean)`

## License

```
   Copyright 2023 SooYoungJang

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

``` 


