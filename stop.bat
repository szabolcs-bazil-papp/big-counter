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
echo Stopping instances

setlocal enabledelayedexpansion

for /r %%i in (.\*.pid) do (
    set /p pid=<%%i
    taskkill /F /PID !pid!
    echo Process with PID !pid! killed.
    del %%i
    echo Removed associated PID file
)

endlocal

echo Instances stopped.
