# Assignment 4 analysis

Student: Turar Dyumbayev (SE-2535). Language: Java 17.

## Algorithms

`lcs` stores the optimum for suffixes `a[i..]` and `b[j..]`; equal characters extend the diagonal and a mismatch takes the larger neighboring state. Walking from `(0,0)` with the same transitions reconstructs an optimal subsequence. Time and auxiliary memory are O(|a||b|).

`merge` uses interval DP. `dp[l][r]` is the minimum cost to merge the closed original interval; each split k combines optimal children and adds the interval sum. A second split table is recursively emitted postorder, so every child merge precedes its parent. Time O(n^3), memory O(n^2), and costs use `long`.

`tree` roots the tree at 0 with an iterative traversal. `take[u] = weight[u] + sum(skip[child])`, while `skip[u] = sum(max(take[child],skip[child]))`. Reconstruction carries only whether the parent was selected, so a selected vertex blocks its children but a skipped vertex does not. Time and memory are O(n), with no recursion.

`naive` checks every valid start directly, including overlaps and all boundaries for an empty pattern. `kmp` builds the prefix function and falls back to the longest proper prefix after mismatches and matches. `rk` uses base 31, modulus 101, rolling updates, and character verification after equal hashes. Their output lists are exact.

`suffix` constructs nonempty suffixes by prefix doubling with rank-pair sorting, then computes adjacent LCP values with Kasai's reuse invariant. The maximum LCP yields a longest repeated substring. It does not materialize suffix strings.

`aho` builds one trie, breadth-first failure links, and complete transition rows. For each independent text it counts visits at states, propagates counts in reverse BFS failure order, and reads each terminal state for every original dictionary position; duplicates therefore receive equal counts.

## Own tests and verification

The example cases were run as one mixed JSONL process, including empty strings, overlaps, the `ab`/`dj` hash collision, banana suffixes, duplicate/suffix Aho patterns, all-negative tree weights, and merge reconstruction. Public launcher tests could not be run because this student folder did not contain the official template's `tools/`, `tests/`, and documentation at the start of work; the implementation was checked with Java 17 compilation and mixed examples. No timings are claimed here because no benchmark run was completed.

## Sources and assistance

The official assignment template and its contracts were used as the interface and requirements source. AI assistance was used to implement and review the algorithms; the student remains responsible for understanding, checking, and verifying the work. The individual Defence Quiz was not taken.

## Official verification and benchmark

The official `tools/`, `tests/`, `Java/source/` launcher files, README, assignment specification, contracts, rubric, testing guide and submission checklist were restored from the public template. `submission.json` selects `Java`.

The official Python commands were attempted on 8 November 2026, but this Windows environment has only the Python launcher stub and no installed Python runtime: both `py -3 tools/run.py --language Java --setup` and `py -3 tools/run.py --language Java --check` exited 112 with `No installed Python found!`. As an independent runtime check, Java 17 compilation succeeded and all 44 official public inputs produced 44 output lines through the official Java launcher. Semantic public-test validation could not be executed without Python, so this is not claimed as 44/44.

Additional test file: `tests/student/boundary.jsonl`, covering empty inputs, zero merges, negative tree weights, overlaps, empty patterns, hash-window searches, repeated suffixes, duplicate Aho patterns and empty texts. The mixed example/boundary runs completed without process errors; the official validator could not be run because Python is unavailable.

Benchmark methodology: one Java process invocation per measurement, already compiled with `javac --release 17`; no compilation time was included in the measured interval, but JVM startup was included equally for every method. Inputs were generated reproducibly in PowerShell as `text = "a" repeated n times` and `pattern = "a" repeated (n/2-1) times followed by `b`, with n/m = 300/150, 900/450 and 1800/900. Each method used the identical text/pattern pair at each size and was run three times. The pattern is absent and creates a useful naive mismatch workload; the small fixed modulus remains exact because RK verifies candidate windows. Environment: Windows PowerShell, Java 17, `-Xmx384m`, one local CPU process. Command shape: `@{task=$method;text=$text;pattern=$pat} | ConvertTo-Json -Compress | java -Xmx384m -cp Java\build Main`. No separate warm-up run was used; the JVM startup cost is therefore included.

Measured wall-clock milliseconds (runs shown in order; median is the middle numeric run after sorting):

| n | m | method | run 1 | run 2 | run 3 | median |
|---:|---:|---|---:|---:|---:|---:|
| 300 | 150 | naive | 873.313 | 565.635 | 547.813 | 565.635 |
| 300 | 150 | kmp | 522.935 | 565.994 | 556.928 | 556.928 |
| 300 | 150 | rk | 570.994 | 548.216 | 512.496 | 548.216 |
| 900 | 450 | naive | 588.301 | 585.843 | 637.787 | 588.301 |
| 900 | 450 | kmp | 636.159 | 683.041 | 584.779 | 636.159 |
| 900 | 450 | rk | 515.928 | 514.424 | 493.234 | 514.424 |
| 1800 | 900 | naive | 561.063 | 576.939 | 536.069 | 561.063 |
| 1800 | 900 | kmp | 560.390 | 563.060 | 516.886 | 560.390 |
| 1800 | 900 | rk | 469.630 | 428.347 | 421.443 | 428.347 |

These timings include process startup and are not an asymptotic ranking; the benchmark is a reproducible record rather than a proof of complexity.
