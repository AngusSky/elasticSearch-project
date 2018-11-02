package com.angus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public abstract class PathPatternMatcher {
    public PathPatternMatcher() {
    }

    public static boolean isPattern(String str) {
        return str.indexOf(42) != -1 || str.indexOf(63) != -1;
    }

    public static boolean match(String pattern, String str) {
        if (str.startsWith("/") != pattern.startsWith("/")) {
            return false;
        } else {
            List patDirs = tokenizePath(pattern);
            List strDirs = tokenizePath(str);
            int patIdxStart = 0;
            int patIdxEnd = patDirs.size() - 1;
            int strIdxStart = 0;

            int strIdxEnd;
            String patDir;
            for(strIdxEnd = strDirs.size() - 1; patIdxStart <= patIdxEnd && strIdxStart <= strIdxEnd; ++strIdxStart) {
                patDir = (String)patDirs.get(patIdxStart);
                if (patDir.equals("**")) {
                    break;
                }

                if (!matchStrings(patDir, (String)strDirs.get(strIdxStart))) {
                    return false;
                }

                ++patIdxStart;
            }

            int patIdxTmp;
            if (strIdxStart > strIdxEnd) {
                for(patIdxTmp = patIdxStart; patIdxTmp <= patIdxEnd; ++patIdxTmp) {
                    if (!patDirs.get(patIdxTmp).equals("**")) {
                        return false;
                    }
                }

                return true;
            } else if (patIdxStart > patIdxEnd) {
                return false;
            } else {
                while(patIdxStart <= patIdxEnd && strIdxStart <= strIdxEnd) {
                    patDir = (String)patDirs.get(patIdxEnd);
                    if (patDir.equals("**")) {
                        break;
                    }

                    if (!matchStrings(patDir, (String)strDirs.get(strIdxEnd))) {
                        return false;
                    }

                    --patIdxEnd;
                    --strIdxEnd;
                }

                if (strIdxStart > strIdxEnd) {
                    for(patIdxTmp = patIdxStart; patIdxTmp <= patIdxEnd; ++patIdxTmp) {
                        if (!patDirs.get(patIdxTmp).equals("**")) {
                            return false;
                        }
                    }

                    return true;
                } else {
                    while(patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
                        patIdxTmp = -1;

                        int patLength;
                        for(patLength = patIdxStart + 1; patLength <= patIdxEnd; ++patLength) {
                            if (patDirs.get(patLength).equals("**")) {
                                patIdxTmp = patLength;
                                break;
                            }
                        }

                        if (patIdxTmp == patIdxStart + 1) {
                            ++patIdxStart;
                        } else {
                            patLength = patIdxTmp - patIdxStart - 1;
                            int strLength = strIdxEnd - strIdxStart + 1;
                            int foundIdx = -1;
                            int i = 0;

                            label110:
                            while(i <= strLength - patLength) {
                                for(int j = 0; j < patLength; ++j) {
                                    String subPat = (String)patDirs.get(patIdxStart + j + 1);
                                    String subStr = (String)strDirs.get(strIdxStart + i + j);
                                    if (!matchStrings(subPat, subStr)) {
                                        ++i;
                                        continue label110;
                                    }
                                }

                                foundIdx = strIdxStart + i;
                                break;
                            }

                            if (foundIdx == -1) {
                                return false;
                            }

                            patIdxStart = patIdxTmp;
                            strIdxStart = foundIdx + patLength;
                        }
                    }

                    for(patIdxTmp = patIdxStart; patIdxTmp <= patIdxEnd; ++patIdxTmp) {
                        if (!patDirs.get(patIdxTmp).equals("**")) {
                            return false;
                        }
                    }

                    return true;
                }
            }
        }
    }

    private static boolean matchStrings(String pattern, String str) {
        char[] patArr = pattern.toCharArray();
        char[] strArr = str.toCharArray();
        int patIdxStart = 0;
        int patIdxEnd = patArr.length - 1;
        int strIdxStart = 0;
        int strIdxEnd = strArr.length - 1;
        boolean containsStar = false;

        int patIdxTmp;
        for(patIdxTmp = 0; patIdxTmp < patArr.length; ++patIdxTmp) {
            if (patArr[patIdxTmp] == '*') {
                containsStar = true;
                break;
            }
        }

        char ch;
        if (!containsStar) {
            if (patIdxEnd != strIdxEnd) {
                return false;
            } else {
                for(patIdxTmp = 0; patIdxTmp <= patIdxEnd; ++patIdxTmp) {
                    ch = patArr[patIdxTmp];
                    if (ch != '?' && ch != strArr[patIdxTmp]) {
                        return false;
                    }
                }

                return true;
            }
        } else if (patIdxEnd == 0) {
            return true;
        } else {
            while((ch = patArr[patIdxStart]) != '*' && strIdxStart <= strIdxEnd) {
                if (ch != '?' && ch != strArr[strIdxStart]) {
                    return false;
                }

                ++patIdxStart;
                ++strIdxStart;
            }

            if (strIdxStart > strIdxEnd) {
                for(patIdxTmp = patIdxStart; patIdxTmp <= patIdxEnd; ++patIdxTmp) {
                    if (patArr[patIdxTmp] != '*') {
                        return false;
                    }
                }

                return true;
            } else {
                while((ch = patArr[patIdxEnd]) != '*' && strIdxStart <= strIdxEnd) {
                    if (ch != '?' && ch != strArr[strIdxEnd]) {
                        return false;
                    }

                    --patIdxEnd;
                    --strIdxEnd;
                }

                if (strIdxStart > strIdxEnd) {
                    for(patIdxTmp = patIdxStart; patIdxTmp <= patIdxEnd; ++patIdxTmp) {
                        if (patArr[patIdxTmp] != '*') {
                            return false;
                        }
                    }

                    return true;
                } else {
                    while(patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
                        patIdxTmp = -1;

                        int patLength;
                        for(patLength = patIdxStart + 1; patLength <= patIdxEnd; ++patLength) {
                            if (patArr[patLength] == '*') {
                                patIdxTmp = patLength;
                                break;
                            }
                        }

                        if (patIdxTmp == patIdxStart + 1) {
                            ++patIdxStart;
                        } else {
                            patLength = patIdxTmp - patIdxStart - 1;
                            int strLength = strIdxEnd - strIdxStart + 1;
                            int foundIdx = -1;
                            int i = 0;

                            label132:
                            while(i <= strLength - patLength) {
                                for(int j = 0; j < patLength; ++j) {
                                    ch = patArr[patIdxStart + j + 1];
                                    if (ch != '?' && ch != strArr[strIdxStart + i + j]) {
                                        ++i;
                                        continue label132;
                                    }
                                }

                                foundIdx = strIdxStart + i;
                                break;
                            }

                            if (foundIdx == -1) {
                                return false;
                            }

                            patIdxStart = patIdxTmp;
                            strIdxStart = foundIdx + patLength;
                        }
                    }

                    for(patIdxTmp = patIdxStart; patIdxTmp <= patIdxEnd; ++patIdxTmp) {
                        if (patArr[patIdxTmp] != '*') {
                            return false;
                        }
                    }

                    return true;
                }
            }
        }
    }

    private static List tokenizePath(String path) {
        List ret = new ArrayList();
        StringTokenizer st = new StringTokenizer(path, "/");

        while(st.hasMoreTokens()) {
            ret.add(st.nextToken());
        }

        return ret;
    }

    public static boolean urlPathMatch(List<String> pathes, String urlPath) {
        Iterator i$ = pathes.iterator();

        boolean f;
        do {
            if (!i$.hasNext()) {
                return false;
            }

            String path = (String)i$.next();
            f = false;
            if (isPattern(path)) {
                f = match(path, urlPath);
            } else {
                f = urlPath.equals(path);
            }
        } while(!f);

        return true;
    }
}