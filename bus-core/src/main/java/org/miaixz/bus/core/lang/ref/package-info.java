/**
 * 引用工具封装，主要针对{@link java.lang.ref.Reference} 工具化封装
 * 主要封装包括：
 * <pre>
 * 1. {@link java.lang.ref.SoftReference} 软引用，在GC报告内存不足时会被GC回收
 * 2. {@link java.lang.ref.WeakReference} 弱引用，在GC时发现弱引用会回收其对象
 * 3. {@link java.lang.ref.PhantomReference} 虚引用，在GC时发现虚引用对象，会将{@link java.lang.ref.PhantomReference}插入{@link java.lang.ref.ReferenceQueue}。 此时对象未被真正回收，要等到{@link java.lang.ref.ReferenceQueue}被真正处理后才会被回收。
 * </pre>
 *
 * @author Kimi Liu
 * @since Java 17+
 */
package org.miaixz.bus.core.lang.ref;
