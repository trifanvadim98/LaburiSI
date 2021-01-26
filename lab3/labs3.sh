#!/bin/bash
declare -A matrix
read num_rows
read num_columns

for ((i=1;i<=num_rows;i++)) do
    for ((j=1;j<=num_columns;j++)) do
            read matrix[$((i * num_rows + j))]
    done
done

f1="%$((${#num_rows}+1))s"
f2=" %9s"

printf "$f1" ''
for ((i=1;i<=num_rows;i++)) do
    printf "$f2" $i
done
echo

for ((j=1;j<=num_columns;j++)) do
    printf "$f1" $j
    for ((i=1;i<=num_rows;i++)) do
        printf "$f2" ${matrix[$((i * num_rows + j))]}
    done
    echo
done






