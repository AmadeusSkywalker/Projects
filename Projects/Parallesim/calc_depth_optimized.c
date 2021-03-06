/*
 * Project 2: Performance Optimization
 */
#if !defined(_MSC_VER)
#include <pthread.h>
#endif
#include <omp.h>

#if defined(_MSC_VER)
#include <intrin.h>
#elif defined(__GNUC__) && (defined(__x86_64__) || defined(__i386__))
#include <x86intrin.h>
#endif

#include <math.h>
#include <limits.h>
#include <stdbool.h>
#include <stddef.h>
#include <stdio.h>

#include "calc_depth_naive.h"
#include "calc_depth_optimized.h"
#include "utils.h"

void calc_depth_optimized(float *depth, float *left, float *right,
        int image_width, int image_height, int feature_width,
        int feature_height, int maximum_displacement) {
    // Naive implementation
    #pragma omp parallel for
    for (int y = 0; y < image_height; y++) {
        for (int x = 0; x < image_width; x++) {
            if (y < feature_height || y >= image_height - feature_height
                    || x < feature_width || x >= image_width - feature_width) {
                depth[y * image_width + x] = 0;
                continue;
            }
            float min_diff = -1;
            int min_dy = 0;
            int min_dx = 0;
            for (int dy = -maximum_displacement; dy <= maximum_displacement; dy++) {
                for (int dx = -maximum_displacement; dx <= maximum_displacement; dx++) {
                    if (y + dy - feature_height < 0
                        || y + dy + feature_height >= image_height
                        || x + dx - feature_width < 0
                        || x + dx + feature_width >= image_width) {
                        continue;
                    }
                    float squared_diff = 0;
                    int chunck = (2*feature_width+1)/4*4;
                    for (int box_y = -feature_height; box_y <= feature_height; box_y++) {
                        __m128 SquaredD = _mm_setzero_ps();
                        for (int box_x = -feature_width; box_x < chunck - feature_width; box_x+=4) {
                            int left_x = x + box_x;
                            int left_y = y + box_y;
                            int right_x = x + dx + box_x;
                            int right_y = y + dy + box_y;
                            int leftIndex=left_y * image_width + left_x;
                            int rightIndex=right_y * image_width + right_x;
                            __m128 Diff = _mm_sub_ps(_mm_loadu_ps(left + leftIndex), _mm_loadu_ps(right + rightIndex));
                            SquaredD = _mm_add_ps(SquaredD, _mm_mul_ps(Diff, Diff));
                        }
                        float array[4];
                        _mm_storeu_ps(array, SquaredD);
                        for(int box_x = chunck - feature_width; box_x <= feature_width; box_x++){
                            int left_x = x + box_x;
                            int left_y = y + box_y;
                            int right_x = x + dx + box_x;
                            int right_y = y + dy + box_y;
                            int leftIndex=left_y * image_width + left_x;
                            int rightIndex=right_y * image_width + right_x;
                            array[0] += (left[leftIndex] - right[rightIndex]) * (left[leftIndex] - right[rightIndex]);
                        }
                        squared_diff += (array[0]+array[1]+array[2]+array[3]);
                    }
                    if (min_diff == -1 || min_diff > squared_diff
                        || (min_diff == squared_diff
                            && sqrt(dx * dx + dy * dy) < sqrt (min_dx *min_dx + min_dy*min_dy))) {
                            min_diff = squared_diff;
                            min_dx = dx;
                            min_dy = dy;
                        }
                }
            }
            if (min_diff != -1) {
                if (maximum_displacement == 0) {
                    depth[y * image_width + x] = 0;
                } else {
                    depth[y * image_width + x] = sqrt(min_dx * min_dx + min_dy * min_dy);
                }
            } else {
                depth[y * image_width + x] = 0;
            }
        }
    }
}
