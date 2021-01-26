#include <stdio.h>
#include <stdlib.h>

void UmpleArray(int **array);
void AfiseazaArray(int **array);

int main()
{
    int **array;

    /*alocare memorie */
    array= malloc(10*sizeof(int *));
    for (int i=0; i<10; i++) {
        array[i] = malloc(10*sizeof(int));
    }
    /*alocare memorie */

    UmpleArray(array);
    AfiseazaArray(array);

    /*eliberarea memorie */
    for (int i=0; i<10; i++) {
    free(array[i]);
    }
    free(array);
    /*eliberarea memorie */

    /*alocare memorie */
    //array= malloc(10*sizeof(int *));
    //for (int i=0; i<10; i++) {
    //   array[i] = malloc(10*sizeof(int));
    //}
    /*alocare memorie */

    AfiseazaArray(array);
   return 0;
}

void UmpleArray(int **array)
{
  int count;
  int i,j;
    count = 0;
    for (i = 0; i <  10; i++)
      for (j = 0; j < 10; j++)
         array[i][j] = ++count;
}
void AfiseazaArray(int **array)
{
    int i,j;
        for (i = 0; i <  10; i++)
        {
        printf("\n");
            for (j = 0; j < 10; j++)
                printf("%d ", array[i][j]);
        }
}
