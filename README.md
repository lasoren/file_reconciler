file_reconciler
===============

Advanced Data Structures (ENG EC504 at Boston University) final project to reconcile arbitrary files between computers over the air with maximal efficiency and minimal bandwidth. 

## Features

Reconcile (make the same, restore discrepancies) any number of arbitrary files (text or binary) between computers. 

Algorithm is fast and efficient especially for very large files with a small number of changes. A 100 MB file with 5 random insertions, deletions, or modifications can be reconciled between two computers by sending less than 20 kB of data.

## Setup

1. Download the ZIP file of this project or fork and clone the project.
2. Navigate to the project directory and run the first command to ensure that the bash script "reconcile" is executable.
3. To run "reconcile" globally (from any directory) enter the second two commands: (enter your admin password when prompted)

        chmod +x reconcile  
        sudo cp reconcile /usr/local/bin/ 
        sudo cp reconcile.jar /usr/local/bin/ 
  
## Usage

Run command on ALL machines to be reconciled.

To reconcile a file between any number of computers or on a local machine:

        reconcile -file [filepath] -to [IP or localhost]
        
To reconcile a directory between any number of computers or on a local machine:

        reconcile -file [full directory path] -to [IP or localhost]
        
The computer with the most up-to-date files should run the reconcile program FIRST. As many desired clients can then connect to this computer to reconcile files or directories of files.

