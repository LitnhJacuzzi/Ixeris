# Ixeris

## Overview

Ixeris is a mod that optimizes the client performance by offloading event polling to a separate thread, making available more CPU time for the render thread.

You might have noticed a visible drop of the FPS when you move your mouse. Part of the FPS drop is because the game *does* have additional jobs to do when you turn the camera, like calculating the visibility of chunks. However, the native code that polls events is not always efficient enough, and some of the CPU time, otherwize can be utilized for rendering, are unnecessarily spent on the call to ```glfwPollEvents()```. This is most noticeable on Windows, especially when your mouse has a high polling rate.

This mod resolves this issue by moving the ```glfwPollEvents()``` call to a separate thread. This means the rendering thread will no longer be blocked when GLFW retrieves events from the operating system, and more CPU time is available for rendering. FPS improvements while standing still are unlikely, but you will have a much smoother framerate when turning the camera.

## Benchmarks

These tests are done after the world has fully loaded and the framerate has stabilized. The mouse has a polling rate of 1000Hz. ```F3+Esc``` is pressed to make sure the framerate change when moving the mouse is the result of event polling, not any other calculation.

The "Idle FPS" column shows the FPS when not moving the mouse. The next two columns show the FPS when moving the mouse quickly over the game window, without Ixeris and with Ixeris, respectively. The last column compares the FPS when Ixeris is and is not installed.

|                 | Idle FPS | Without Ixeris | With Ixeris | Improvement |
|-----------------|----------|----------------|-------------|-------------|
| Windows         | 233 FPS  | 133 FPS        | 165 FPS     | 1.24x       |
| Linux (X11)     | 358 FPS  | 320 FPS        | 355 FPS     | 1.11x       |
| Linux (Wayland) | 364 FPS  | 289 FPS        | 298 FPS     | 1.03x       |

## Thread Safety (Technical)

Efforts have been made to make sure Ixeris does not break thread safety. Callbacks registered with ```glfwSet*Callback``` are executed on the render thread. Calls to GLFW functions that are required to be called on the main thread, if made on other threads, are dispatched to the main thread. They may immediately return (or wait until the main thread finishes execution, if ```fullyBlockingMode``` is set to true in the config) if they do not return any value, or otherwise may be blocked until the return value is retrieved from the main thread.

As of version 3.1.0, the requirements of thread safety in the GLFW documentation are strictly obeyed.
