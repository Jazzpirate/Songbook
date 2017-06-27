# Absolem's Treasure Chest

This is a modular songbook using latex's songs-package with a simple java-script to generate the index, chapters etc. automatically. 
Its contents are automatically assembled from individual .tex files in the `bands`-folder.

### Usage
- For every artist, create a new file `<bandid>.tex` in the `bands`-folder. This file should contain the individual songs 
  by that artist. Note, that the artists will be sorted by the filename (i.e. `<bandid>`), not the actual name of the artist. This
  allows for sorting e.g. `Steven Wilson` under `W`, by naming the corresponding file e.g. `wilson_steven.tex`.
- The first line of an artist's file needs to contain the full name of the artist as a comment. E.g. the file 
  `wilson_steven.tex` would have as first line `% Steven Wilson`. This is the name that will actually appear 
  in the final document.
- Add the individual songs to the artist's file using the syntax provided by the songs package.
  Details on the syntax can be found in the [official documentation](http://songs.sourceforge.net/) of the package.
  Note, that the songs will appar in the final document in the same order as they are in the artists `.tex`-file.
- Having done so, simply run the `build.jar` (usually via `java -jar build.jar`), which should take care of all the rest for you
  by assembling the individual band files into one `main.tex` with an appropriate table of contents etc. In case of
  errors, you can manually compile the `main.tex` to get proper error messages.
