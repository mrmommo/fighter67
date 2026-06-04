package com.nhom67.platformfighter.util;

import com.almasb.fxgl.app.ReadOnlyGameSettings;
import com.almasb.fxgl.dsl.FXGL;
import java.lang.reflect.Method;

public class TestReflection {
    public static void main(String[] args) {
        System.out.println("Methods in ReadOnlyGameSettings:");
        for (Method m : ReadOnlyGameSettings.class.getMethods()) {
            if (m.getName().toLowerCase().contains("volume") || m.getName().toLowerCase().contains("sound") || m.getName().toLowerCase().contains("music")) {
                System.out.println(m.getName());
            }
        }
    }
}
