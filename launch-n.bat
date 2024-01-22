@rem Copyright (C) 2024 it4all Hungary Kft.
@rem
@rem This program is free software: you can redistribute it and/or modify it under the terms of the
@rem GNU Lesser General Public License as published by the Free Software Foundation, either version 3
@rem of the License, or (at your option) any later version.
@rem
@rem This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
@rem even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
@rem Lesser General Public License for more details.
@rem
@rem You should have received a copy of the GNU Lesser General Public License along with this program.
@rem If not, see <http://www.gnu.org/licenses/>.

@echo off
if "%1"=="" (
    echo Usage: %0 ^<number_of_instances>^
    exit /b 1
)

setlocal enabledelayedexpansion
set instances=%1

echo Copying application to working directory...
copy build\libs\big-counter-1.0.0.jar .

echo Launching commander...
start /B java -jar .\big-counter-1.0.0.jar -c -sv -n Commander > commander.log 2>&1

echo Launching %instances% followers...
for /L %%i in (1,1,%instances%) do (
    start /B java -jar .\big-counter-1.0.0.jar -e -sv -n Follower%%i > follower-%%i.log 2>&1
)

echo Running!
