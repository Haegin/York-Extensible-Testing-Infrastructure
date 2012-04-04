#!/bin/zsh

WIDTH=30
DOC_HDR="Document"

echo
# Pads the document header to the right width for the table
echo " ${(r:${WIDTH}:)DOC_HDR} | Word Count"
# Add 13 as this is the length of ' | Word Count'
echo " ${(l:$(( ${WIDTH} + 13 ))::-:)}"
for doc in **/*.tex; do
    # remove the .tex extension, the folder names and replace - with ' '
    shortdoc=$(basename ${doc//-/ } .tex)
    # print the name of the document, padded to 30 chars, followed by the word count
    echo " ${(r:${WIDTH}:)shortdoc} | $(cat "${doc}" | detex 2> /dev/null | wc -w)" 2> /dev/null
done
echo
