package me.decce.ixeris.glfw.state_caching.window;

import me.decce.ixeris.glfw.callback_stack.FramebufferSizeCallbackStack;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

public class GlfwFramebufferSizeCache extends GlfwWindowCache {
    public static final int VALUE_UNINITIALIZED = -1;
    private int width = VALUE_UNINITIALIZED;
    private int height = VALUE_UNINITIALIZED;

    public GlfwFramebufferSizeCache(long window) {
        super(window);
        FramebufferSizeCallbackStack.get(window).registerMainThreadCallback(this::onFramebufferSizeCallback);
        this.enableCache();
    }

    private void onFramebufferSizeCallback(long window, int width, int height) {
        if (this.window == window) {
            this.width = width;
            this.height = height;
        }
    }

    public void get(int[] width, int[] height) {
        if (this.width == VALUE_UNINITIALIZED || this.height == VALUE_UNINITIALIZED) {
            blockingGet();
        }
        width[0] = this.width;
        height[0] = this.height;
    }

    public void get(IntBuffer width, IntBuffer height) {
        if (this.width == VALUE_UNINITIALIZED || this.height == VALUE_UNINITIALIZED) {
            blockingGet();
        }
        width.put(this.width).flip();
        height.put(this.height).flip();
    }

    private void blockingGet() {
        this.disableCache();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            GLFW.glfwGetFramebufferSize(window, width, height);
            this.width = width.get();
            this.height = height.get();
        }
        this.enableCache();
    }
}
