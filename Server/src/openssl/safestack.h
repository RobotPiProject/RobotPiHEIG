/*
 * WARNING: do not edit!
 * Generated by Makefile from include/openssl/safestack.h.in
 *
 * Copyright 1999-2020 The OpenSSL Project Authors. All Rights Reserved.
 *
 * Licensed under the Apache License 2.0 (the "License").  You may not use
 * this file except in compliance with the License.  You can obtain a copy
 * in the file LICENSE in the source distribution or at
 * https://www.openssl.org/source/license.html
 */



#ifndef OPENSSL_SAFESTACK_H
# define OPENSSL_SAFESTACK_H
# pragma once

# include <openssl/macros.h>
# ifndef OPENSSL_NO_DEPRECATED_3_0
#  define HEADER_SAFESTACK_H
# endif

# include <openssl/stack.h>
# include <openssl/e_os2.h>

#ifdef __cplusplus
extern "C" {
#endif

# define STACK_OF(type) struct stack_st_##type

/* Helper macro for internal use */
# define SKM_DEFINE_STACK_OF_INTERNAL(t1, t2, t3) \
    STACK_OF(t1); \
    typedef int (*sk_##t1##_compfunc)(const t3 * const *a, const t3 *const *b); \
    typedef void (*sk_##t1##_freefunc)(t3 *a); \
    typedef t3 * (*sk_##t1##_copyfunc)(const t3 *a); \
    static ossl_unused ossl_inline t2 *ossl_check_##t1##_type(t2 *ptr) \
    { \
        return ptr; \
    } \
    static ossl_unused ossl_inline const OPENSSL_STACK *ossl_check_const_##t1##_sk_type(const STACK_OF(t1) *sk) \
    { \
        return (const OPENSSL_STACK *)sk; \
    } \
    static ossl_unused ossl_inline OPENSSL_STACK *ossl_check_##t1##_sk_type(STACK_OF(t1) *sk) \
    { \
        return (OPENSSL_STACK *)sk; \
    } \
    static ossl_unused ossl_inline OPENSSL_sk_compfunc ossl_check_##t1##_compfunc_type(sk_##t1##_compfunc cmp) \
    { \
        return (OPENSSL_sk_compfunc)cmp; \
    } \
    static ossl_unused ossl_inline OPENSSL_sk_copyfunc ossl_check_##t1##_copyfunc_type(sk_##t1##_copyfunc cpy) \
    { \
        return (OPENSSL_sk_copyfunc)cpy; \
    } \
    static ossl_unused ossl_inline OPENSSL_sk_freefunc ossl_check_##t1##_freefunc_type(sk_##t1##_freefunc fr) \
    { \
        return (OPENSSL_sk_freefunc)fr; \
    }

# define SKM_DEFINE_STACK_OF(t1, t2, t3) \
    STACK_OF(t1); \
    typedef int (*sk_##t1##_compfunc)(const t3 * const *a, const t3 *const *b); \
    typedef void (*sk_##t1##_freefunc)(t3 *a); \
    typedef t3 * (*sk_##t1##_copyfunc)(const t3 *a); \
    static ossl_unused ossl_inline int sk_##t1##_num(const STACK_OF(t1) *sk) \
    { \
        return OPENSSL_sk_num((const OPENSSL_STACK *)sk); \
    } \
    static ossl_unused ossl_inline t2 *sk_##t1##_value(const STACK_OF(t1) *sk, int idx) \
    { \
        return (t2 *)OPENSSL_sk_value((const OPENSSL_STACK *)sk, idx); \
    } \
    static ossl_unused ossl_inline STACK_OF(t1) *sk_##t1##_new(sk_##t1##_compfunc compare) \
    { \
        return (STACK_OF(t1) *)OPENSSL_sk_new((OPENSSL_sk_compfunc)compare); \
    } \
    static ossl_unused ossl_inline STACK_OF(t1) *sk_##t1##_new_null(void) \
    { \
        return (STACK_OF(t1) *)OPENSSL_sk_new_null(); \
    } \
    static ossl_unused ossl_inline STACK_OF(t1) *sk_##t1##_new_reserve(sk_##t1##_compfunc compare, int n) \
    { \
        return (STACK_OF(t1) *)OPENSSL_sk_new_reserve((OPENSSL_sk_compfunc)compare, n); \
    } \
    static ossl_unused ossl_inline int sk_##t1##_reserve(STACK_OF(t1) *sk, int n) \
    { \
        return OPENSSL_sk_reserve((OPENSSL_STACK *)sk, n); \
    } \
    static ossl_unused ossl_inline void sk_##t1##_free(STACK_OF(t1) *sk) \
    { \
        OPENSSL_sk_free((OPENSSL_STACK *)sk); \
    } \
    static ossl_unused ossl_inline void sk_##t1##_zero(STACK_OF(t1) *sk) \
    { \
        OPENSSL_sk_zero((OPENSSL_STACK *)sk); \
    } \
    static ossl_unused ossl_inline t2 *sk_##t1##_delete(STACK_OF(t1) *sk, int i) \
    { \
        return (t2 *)OPENSSL_sk_delete((OPENSSL_STACK *)sk, i); \
    } \
    static ossl_unused ossl_inline t2 *sk_##t1##_delete_ptr(STACK_OF(t1) *sk, t2 *ptr) \
    { \
        return (t2 *)OPENSSL_sk_delete_ptr((OPENSSL_STACK *)sk, \
                                           (const void *)ptr); \
    } \
    static ossl_unused ossl_inline int sk_##t1##_push(STACK_OF(t1) *sk, t2 *ptr) \
    { \
        return OPENSSL_sk_push((OPENSSL_STACK *)sk, (const void *)ptr); \
    } \
    static ossl_unused ossl_inline int sk_##t1##_unshift(STACK_OF(t1) *sk, t2 *ptr) \
    { \
        return OPENSSL_sk_unshift((OPENSSL_STACK *)sk, (const void *)ptr); \
    } \
    static ossl_unused ossl_inline t2 *sk_##t1##_pop(STACK_OF(t1) *sk) \
    { \
        return (t2 *)OPENSSL_sk_pop((OPENSSL_STACK *)sk); \
    } \
    static ossl_unused ossl_inline t2 *sk_##t1##_shift(STACK_OF(t1) *sk) \
    { \
        return (t2 *)OPENSSL_sk_shift((OPENSSL_STACK *)sk); \
    } \
    static ossl_unused ossl_inline void sk_##t1##_pop_free(STACK_OF(t1) *sk, sk_##t1##_freefunc freefunc) \
    { \
        OPENSSL_sk_pop_free((OPENSSL_STACK *)sk, (OPENSSL_sk_freefunc)freefunc); \
    } \
    static ossl_unused ossl_inline int sk_##t1##_insert(STACK_OF(t1) *sk, t2 *ptr, int idx) \
    { \
        return OPENSSL_sk_insert((OPENSSL_STACK *)sk, (const void *)ptr, idx); \
    } \
    static ossl_unused ossl_inline t2 *sk_##t1##_set(STACK_OF(t1) *sk, int idx, t2 *ptr) \
    { \
        return (t2 *)OPENSSL_sk_set((OPENSSL_STACK *)sk, idx, (const void *)ptr); \
    } \
    static ossl_unused ossl_inline int sk_##t1##_find(STACK_OF(t1) *sk, t2 *ptr) \
    { \
        return OPENSSL_sk_find((OPENSSL_STACK *)sk, (const void *)ptr); \
    } \
    static ossl_unused ossl_inline int sk_##t1##_find_ex(STACK_OF(t1) *sk, t2 *ptr) \
    { \
        return OPENSSL_sk_find_ex((OPENSSL_STACK *)sk, (const void *)ptr); \
    } \
    static ossl_unused ossl_inline int sk_##t1##_find_all(STACK_OF(t1) *sk, t2 *ptr, int *pnum) \
    { \
        return OPENSSL_sk_find_all((OPENSSL_STACK *)sk, (const void *)ptr, pnum); \
    } \
    static ossl_unused ossl_inline void sk_##t1##_sort(STACK_OF(t1) *sk) \
    { \
        OPENSSL_sk_sort((OPENSSL_STACK *)sk); \
    } \
    static ossl_unused ossl_inline int sk_##t1##_is_sorted(const STACK_OF(t1) *sk) \
    { \
        return OPENSSL_sk_is_sorted((const OPENSSL_STACK *)sk); \
    } \
    static ossl_unused ossl_inline STACK_OF(t1) * sk_##t1##_dup(const STACK_OF(t1) *sk) \
    { \
        return (STACK_OF(t1) *)OPENSSL_sk_dup((const OPENSSL_STACK *)sk); \
    } \
    static ossl_unused ossl_inline STACK_OF(t1) *sk_##t1##_deep_copy(const STACK_OF(t1) *sk, \
                                                    sk_##t1##_copyfunc copyfunc, \
                                                    sk_##t1##_freefunc freefunc) \
    { \
        return (STACK_OF(t1) *)OPENSSL_sk_deep_copy((const OPENSSL_STACK *)sk, \
                                            (OPENSSL_sk_copyfunc)copyfunc, \
                                            (OPENSSL_sk_freefunc)freefunc); \
    } \
    static ossl_unused ossl_inline sk_##t1##_compfunc sk_##t1##_set_cmp_func(STACK_OF(t1) *sk, sk_##t1##_compfunc compare) \
    { \
        return (sk_##t1##_compfunc)OPENSSL_sk_set_cmp_func((OPENSSL_STACK *)sk, (OPENSSL_sk_compfunc)compare); \
    }

# define DEFINE_STACK_OF(t) SKM_DEFINE_STACK_OF(t, t, t)
# define DEFINE_STACK_OF_CONST(t) SKM_DEFINE_STACK_OF(t, const t, t)
# define DEFINE_SPECIAL_STACK_OF(t1, t2) SKM_DEFINE_STACK_OF(t1, t2, t2)
# define DEFINE_SPECIAL_STACK_OF_CONST(t1, t2) \
            SKM_DEFINE_STACK_OF(t1, const t2, t2)

/*-
 * Strings are special: normally an lhash entry will point to a single
 * (somewhat) mutable object. In the case of strings:
 *
 * a) Instead of a single char, there is an array of chars, NUL-terminated.
 * b) The string may have be immutable.
 *
 * So, they need their own declarations. Especially important for
 * type-checking tools, such as Deputy.
 *
 * In practice, however, it appears to be hard to have a const
 * string. For now, I'm settling for dealing with the fact it is a
 * string at all.
 */
typedef char *OPENSSL_STRING;
typedef const char *OPENSSL_CSTRING;

/*-
 * Confusingly, LHASH_OF(STRING) deals with char ** throughout, but
 * STACK_OF(STRING) is really more like STACK_OF(char), only, as mentioned
 * above, instead of a single char each entry is a NUL-terminated array of
 * chars. So, we have to implement STRING specially for STACK_OF. This is
 * dealt with in the autogenerated macros below.
 */
SKM_DEFINE_STACK_OF_INTERNAL(OPENSSL_STRING, char, char)
#define sk_OPENSSL_STRING_num(sk) OPENSSL_sk_num(ossl_check_const_OPENSSL_STRING_sk_type(sk))
#define sk_OPENSSL_STRING_value(sk, idx) ((char *)OPENSSL_sk_value(ossl_check_const_OPENSSL_STRING_sk_type(sk), (idx)))
#define sk_OPENSSL_STRING_new(cmp) ((STACK_OF(OPENSSL_STRING) *)OPENSSL_sk_new(ossl_check_OPENSSL_STRING_compfunc_type(cmp)))
#define sk_OPENSSL_STRING_new_null() ((STACK_OF(OPENSSL_STRING) *)OPENSSL_sk_new_null())
#define sk_OPENSSL_STRING_new_reserve(cmp, n) ((STACK_OF(OPENSSL_STRING) *)OPENSSL_sk_new_reserve(ossl_check_OPENSSL_STRING_compfunc_type(cmp), (n)))
#define sk_OPENSSL_STRING_reserve(sk, n) OPENSSL_sk_reserve(ossl_check_OPENSSL_STRING_sk_type(sk), (n))
#define sk_OPENSSL_STRING_free(sk) OPENSSL_sk_free(ossl_check_OPENSSL_STRING_sk_type(sk))
#define sk_OPENSSL_STRING_zero(sk) OPENSSL_sk_zero(ossl_check_OPENSSL_STRING_sk_type(sk))
#define sk_OPENSSL_STRING_delete(sk, i) ((char *)OPENSSL_sk_delete(ossl_check_OPENSSL_STRING_sk_type(sk), (i)))
#define sk_OPENSSL_STRING_delete_ptr(sk, ptr) ((char *)OPENSSL_sk_delete_ptr(ossl_check_OPENSSL_STRING_sk_type(sk), ossl_check_OPENSSL_STRING_type(ptr)))
#define sk_OPENSSL_STRING_push(sk, ptr) OPENSSL_sk_push(ossl_check_OPENSSL_STRING_sk_type(sk), ossl_check_OPENSSL_STRING_type(ptr))
#define sk_OPENSSL_STRING_unshift(sk, ptr) OPENSSL_sk_unshift(ossl_check_OPENSSL_STRING_sk_type(sk), ossl_check_OPENSSL_STRING_type(ptr))
#define sk_OPENSSL_STRING_pop(sk) ((char *)OPENSSL_sk_pop(ossl_check_OPENSSL_STRING_sk_type(sk)))
#define sk_OPENSSL_STRING_shift(sk) ((char *)OPENSSL_sk_shift(ossl_check_OPENSSL_STRING_sk_type(sk)))
#define sk_OPENSSL_STRING_pop_free(sk, freefunc) OPENSSL_sk_pop_free(ossl_check_OPENSSL_STRING_sk_type(sk),ossl_check_OPENSSL_STRING_freefunc_type(freefunc))
#define sk_OPENSSL_STRING_insert(sk, ptr, idx) OPENSSL_sk_insert(ossl_check_OPENSSL_STRING_sk_type(sk), ossl_check_OPENSSL_STRING_type(ptr), (idx))
#define sk_OPENSSL_STRING_set(sk, idx, ptr) ((char *)OPENSSL_sk_set(ossl_check_OPENSSL_STRING_sk_type(sk), (idx), ossl_check_OPENSSL_STRING_type(ptr)))
#define sk_OPENSSL_STRING_find(sk, ptr) OPENSSL_sk_find(ossl_check_OPENSSL_STRING_sk_type(sk), ossl_check_OPENSSL_STRING_type(ptr))
#define sk_OPENSSL_STRING_find_ex(sk, ptr) OPENSSL_sk_find_ex(ossl_check_OPENSSL_STRING_sk_type(sk), ossl_check_OPENSSL_STRING_type(ptr))
#define sk_OPENSSL_STRING_find_all(sk, ptr, pnum) OPENSSL_sk_find_all(ossl_check_OPENSSL_STRING_sk_type(sk), ossl_check_OPENSSL_STRING_type(ptr), pnum)
#define sk_OPENSSL_STRING_sort(sk) OPENSSL_sk_sort(ossl_check_OPENSSL_STRING_sk_type(sk))
#define sk_OPENSSL_STRING_is_sorted(sk) OPENSSL_sk_is_sorted(ossl_check_const_OPENSSL_STRING_sk_type(sk))
#define sk_OPENSSL_STRING_dup(sk) ((STACK_OF(OPENSSL_STRING) *)OPENSSL_sk_dup(ossl_check_const_OPENSSL_STRING_sk_type(sk)))
#define sk_OPENSSL_STRING_deep_copy(sk, copyfunc, freefunc) ((STACK_OF(OPENSSL_STRING) *)OPENSSL_sk_deep_copy(ossl_check_const_OPENSSL_STRING_sk_type(sk), ossl_check_OPENSSL_STRING_copyfunc_type(copyfunc), ossl_check_OPENSSL_STRING_freefunc_type(freefunc)))
#define sk_OPENSSL_STRING_set_cmp_func(sk, cmp) ((sk_OPENSSL_STRING_compfunc)OPENSSL_sk_set_cmp_func(ossl_check_OPENSSL_STRING_sk_type(sk), ossl_check_OPENSSL_STRING_compfunc_type(cmp)))
SKM_DEFINE_STACK_OF_INTERNAL(OPENSSL_CSTRING, const char, char)
#define sk_OPENSSL_CSTRING_num(sk) OPENSSL_sk_num(ossl_check_const_OPENSSL_CSTRING_sk_type(sk))
#define sk_OPENSSL_CSTRING_value(sk, idx) ((const char *)OPENSSL_sk_value(ossl_check_const_OPENSSL_CSTRING_sk_type(sk), (idx)))
#define sk_OPENSSL_CSTRING_new(cmp) ((STACK_OF(OPENSSL_CSTRING) *)OPENSSL_sk_new(ossl_check_OPENSSL_CSTRING_compfunc_type(cmp)))
#define sk_OPENSSL_CSTRING_new_null() ((STACK_OF(OPENSSL_CSTRING) *)OPENSSL_sk_new_null())
#define sk_OPENSSL_CSTRING_new_reserve(cmp, n) ((STACK_OF(OPENSSL_CSTRING) *)OPENSSL_sk_new_reserve(ossl_check_OPENSSL_CSTRING_compfunc_type(cmp), (n)))
#define sk_OPENSSL_CSTRING_reserve(sk, n) OPENSSL_sk_reserve(ossl_check_OPENSSL_CSTRING_sk_type(sk), (n))
#define sk_OPENSSL_CSTRING_free(sk) OPENSSL_sk_free(ossl_check_OPENSSL_CSTRING_sk_type(sk))
#define sk_OPENSSL_CSTRING_zero(sk) OPENSSL_sk_zero(ossl_check_OPENSSL_CSTRING_sk_type(sk))
#define sk_OPENSSL_CSTRING_delete(sk, i) ((const char *)OPENSSL_sk_delete(ossl_check_OPENSSL_CSTRING_sk_type(sk), (i)))
#define sk_OPENSSL_CSTRING_delete_ptr(sk, ptr) ((const char *)OPENSSL_sk_delete_ptr(ossl_check_OPENSSL_CSTRING_sk_type(sk), ossl_check_OPENSSL_CSTRING_type(ptr)))
#define sk_OPENSSL_CSTRING_push(sk, ptr) OPENSSL_sk_push(ossl_check_OPENSSL_CSTRING_sk_type(sk), ossl_check_OPENSSL_CSTRING_type(ptr))
#define sk_OPENSSL_CSTRING_unshift(sk, ptr) OPENSSL_sk_unshift(ossl_check_OPENSSL_CSTRING_sk_type(sk), ossl_check_OPENSSL_CSTRING_type(ptr))
#define sk_OPENSSL_CSTRING_pop(sk) ((const char *)OPENSSL_sk_pop(ossl_check_OPENSSL_CSTRING_sk_type(sk)))
#define sk_OPENSSL_CSTRING_shift(sk) ((const char *)OPENSSL_sk_shift(ossl_check_OPENSSL_CSTRING_sk_type(sk)))
#define sk_OPENSSL_CSTRING_pop_free(sk, freefunc) OPENSSL_sk_pop_free(ossl_check_OPENSSL_CSTRING_sk_type(sk),ossl_check_OPENSSL_CSTRING_freefunc_type(freefunc))
#define sk_OPENSSL_CSTRING_insert(sk, ptr, idx) OPENSSL_sk_insert(ossl_check_OPENSSL_CSTRING_sk_type(sk), ossl_check_OPENSSL_CSTRING_type(ptr), (idx))
#define sk_OPENSSL_CSTRING_set(sk, idx, ptr) ((const char *)OPENSSL_sk_set(ossl_check_OPENSSL_CSTRING_sk_type(sk), (idx), ossl_check_OPENSSL_CSTRING_type(ptr)))
#define sk_OPENSSL_CSTRING_find(sk, ptr) OPENSSL_sk_find(ossl_check_OPENSSL_CSTRING_sk_type(sk), ossl_check_OPENSSL_CSTRING_type(ptr))
#define sk_OPENSSL_CSTRING_find_ex(sk, ptr) OPENSSL_sk_find_ex(ossl_check_OPENSSL_CSTRING_sk_type(sk), ossl_check_OPENSSL_CSTRING_type(ptr))
#define sk_OPENSSL_CSTRING_find_all(sk, ptr, pnum) OPENSSL_sk_find_all(ossl_check_OPENSSL_CSTRING_sk_type(sk), ossl_check_OPENSSL_CSTRING_type(ptr), pnum)
#define sk_OPENSSL_CSTRING_sort(sk) OPENSSL_sk_sort(ossl_check_OPENSSL_CSTRING_sk_type(sk))
#define sk_OPENSSL_CSTRING_is_sorted(sk) OPENSSL_sk_is_sorted(ossl_check_const_OPENSSL_CSTRING_sk_type(sk))
#define sk_OPENSSL_CSTRING_dup(sk) ((STACK_OF(OPENSSL_CSTRING) *)OPENSSL_sk_dup(ossl_check_const_OPENSSL_CSTRING_sk_type(sk)))
#define sk_OPENSSL_CSTRING_deep_copy(sk, copyfunc, freefunc) ((STACK_OF(OPENSSL_CSTRING) *)OPENSSL_sk_deep_copy(ossl_check_const_OPENSSL_CSTRING_sk_type(sk), ossl_check_OPENSSL_CSTRING_copyfunc_type(copyfunc), ossl_check_OPENSSL_CSTRING_freefunc_type(freefunc)))
#define sk_OPENSSL_CSTRING_set_cmp_func(sk, cmp) ((sk_OPENSSL_CSTRING_compfunc)OPENSSL_sk_set_cmp_func(ossl_check_OPENSSL_CSTRING_sk_type(sk), ossl_check_OPENSSL_CSTRING_compfunc_type(cmp)))


#if !defined(OPENSSL_NO_DEPRECATED_3_0)
/*
 * This is not used by OpenSSL.  A block of bytes,  NOT nul-terminated.
 * These should also be distinguished from "normal" stacks.
 */
typedef void *OPENSSL_BLOCK;
SKM_DEFINE_STACK_OF_INTERNAL(OPENSSL_BLOCK, void, void)
#define sk_OPENSSL_BLOCK_num(sk) OPENSSL_sk_num(ossl_check_const_OPENSSL_BLOCK_sk_type(sk))
#define sk_OPENSSL_BLOCK_value(sk, idx) ((void *)OPENSSL_sk_value(ossl_check_const_OPENSSL_BLOCK_sk_type(sk), (idx)))
#define sk_OPENSSL_BLOCK_new(cmp) ((STACK_OF(OPENSSL_BLOCK) *)OPENSSL_sk_new(ossl_check_OPENSSL_BLOCK_compfunc_type(cmp)))
#define sk_OPENSSL_BLOCK_new_null() ((STACK_OF(OPENSSL_BLOCK) *)OPENSSL_sk_new_null())
#define sk_OPENSSL_BLOCK_new_reserve(cmp, n) ((STACK_OF(OPENSSL_BLOCK) *)OPENSSL_sk_new_reserve(ossl_check_OPENSSL_BLOCK_compfunc_type(cmp), (n)))
#define sk_OPENSSL_BLOCK_reserve(sk, n) OPENSSL_sk_reserve(ossl_check_OPENSSL_BLOCK_sk_type(sk), (n))
#define sk_OPENSSL_BLOCK_free(sk) OPENSSL_sk_free(ossl_check_OPENSSL_BLOCK_sk_type(sk))
#define sk_OPENSSL_BLOCK_zero(sk) OPENSSL_sk_zero(ossl_check_OPENSSL_BLOCK_sk_type(sk))
#define sk_OPENSSL_BLOCK_delete(sk, i) ((void *)OPENSSL_sk_delete(ossl_check_OPENSSL_BLOCK_sk_type(sk), (i)))
#define sk_OPENSSL_BLOCK_delete_ptr(sk, ptr) ((void *)OPENSSL_sk_delete_ptr(ossl_check_OPENSSL_BLOCK_sk_type(sk), ossl_check_OPENSSL_BLOCK_type(ptr)))
#define sk_OPENSSL_BLOCK_push(sk, ptr) OPENSSL_sk_push(ossl_check_OPENSSL_BLOCK_sk_type(sk), ossl_check_OPENSSL_BLOCK_type(ptr))
#define sk_OPENSSL_BLOCK_unshift(sk, ptr) OPENSSL_sk_unshift(ossl_check_OPENSSL_BLOCK_sk_type(sk), ossl_check_OPENSSL_BLOCK_type(ptr))
#define sk_OPENSSL_BLOCK_pop(sk) ((void *)OPENSSL_sk_pop(ossl_check_OPENSSL_BLOCK_sk_type(sk)))
#define sk_OPENSSL_BLOCK_shift(sk) ((void *)OPENSSL_sk_shift(ossl_check_OPENSSL_BLOCK_sk_type(sk)))
#define sk_OPENSSL_BLOCK_pop_free(sk, freefunc) OPENSSL_sk_pop_free(ossl_check_OPENSSL_BLOCK_sk_type(sk),ossl_check_OPENSSL_BLOCK_freefunc_type(freefunc))
#define sk_OPENSSL_BLOCK_insert(sk, ptr, idx) OPENSSL_sk_insert(ossl_check_OPENSSL_BLOCK_sk_type(sk), ossl_check_OPENSSL_BLOCK_type(ptr), (idx))
#define sk_OPENSSL_BLOCK_set(sk, idx, ptr) ((void *)OPENSSL_sk_set(ossl_check_OPENSSL_BLOCK_sk_type(sk), (idx), ossl_check_OPENSSL_BLOCK_type(ptr)))
#define sk_OPENSSL_BLOCK_find(sk, ptr) OPENSSL_sk_find(ossl_check_OPENSSL_BLOCK_sk_type(sk), ossl_check_OPENSSL_BLOCK_type(ptr))
#define sk_OPENSSL_BLOCK_find_ex(sk, ptr) OPENSSL_sk_find_ex(ossl_check_OPENSSL_BLOCK_sk_type(sk), ossl_check_OPENSSL_BLOCK_type(ptr))
#define sk_OPENSSL_BLOCK_find_all(sk, ptr, pnum) OPENSSL_sk_find_all(ossl_check_OPENSSL_BLOCK_sk_type(sk), ossl_check_OPENSSL_BLOCK_type(ptr), pnum)
#define sk_OPENSSL_BLOCK_sort(sk) OPENSSL_sk_sort(ossl_check_OPENSSL_BLOCK_sk_type(sk))
#define sk_OPENSSL_BLOCK_is_sorted(sk) OPENSSL_sk_is_sorted(ossl_check_const_OPENSSL_BLOCK_sk_type(sk))
#define sk_OPENSSL_BLOCK_dup(sk) ((STACK_OF(OPENSSL_BLOCK) *)OPENSSL_sk_dup(ossl_check_const_OPENSSL_BLOCK_sk_type(sk)))
#define sk_OPENSSL_BLOCK_deep_copy(sk, copyfunc, freefunc) ((STACK_OF(OPENSSL_BLOCK) *)OPENSSL_sk_deep_copy(ossl_check_const_OPENSSL_BLOCK_sk_type(sk), ossl_check_OPENSSL_BLOCK_copyfunc_type(copyfunc), ossl_check_OPENSSL_BLOCK_freefunc_type(freefunc)))
#define sk_OPENSSL_BLOCK_set_cmp_func(sk, cmp) ((sk_OPENSSL_BLOCK_compfunc)OPENSSL_sk_set_cmp_func(ossl_check_OPENSSL_BLOCK_sk_type(sk), ossl_check_OPENSSL_BLOCK_compfunc_type(cmp)))

#endif

# ifdef  __cplusplus
}
# endif
#endif
