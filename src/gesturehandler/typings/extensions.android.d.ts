/* eslint-disable @typescript-eslint/no-unnecessary-qualifier */
declare namespace com {
    export namespace nativescript {
        export namespace gesturehandler {
            class GestureHandlerInteractionController extends com.swmansion.gesturehandler.GestureHandlerInteractionController {
                configureInteractions(handler: com.swmansion.gesturehandler.GestureHandler<any>, waitFor: number[], simultaneousHandlers: number[]);
            }
            class RootViewGestureHandler extends com.swmansion.gesturehandler.GestureHandler<RootViewGestureHandler> {}
            class PageLayout extends org.nativescript.widgets.GridLayout {
                constructor(context, rootGestureTag);
                initialize();
                tearDown();
                registry(): com.swmansion.gesturehandler.GestureHandlerRegistryImpl;
                getRootGestureTag(): number;
            }
        }
    }
}
